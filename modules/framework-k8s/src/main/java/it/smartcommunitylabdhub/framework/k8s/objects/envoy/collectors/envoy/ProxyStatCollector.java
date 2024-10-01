package it.smartcommunitylabdhub.framework.k8s.objects.envoy.collectors.envoy;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.framework.k8s.jackson.IntOrStringMixin;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreMetric;

@Component
public class ProxyStatCollector {

    private final ObjectMapper mapper = JacksonMapper.CUSTOM_OBJECT_MAPPER.addMixIn(
            IntOrString.class,
            IntOrStringMixin.class);

    public EnvoyStatData collectEnvoyStats(List<CoreMetric> coreMetricsList, Run run) {
        Map<String, Quantity> aggregatedMetrics = new HashMap<>();

        // get previous stats
        EnvoyStatData prevEnvoyStatData = mapper.convertValue(
                run.getStatus().getOrDefault("proxy", new HashMap<>()), EnvoyStatData.class);

        Optional<CoreMetric> metric = coreMetricsList.stream().findFirst();


        return metric.map(coreMetric -> {
            coreMetric.metrics().stream().flatMap(containerMetric -> containerMetric.getUsage().entrySet().stream())
                    .forEach(entry -> aggregatedMetrics.put(entry.getKey(), entry.getValue()));

            Long previousTotalRequests = Optional.ofNullable(prevEnvoyStatData.getTotalRequests()).orElse(0L);
            Long totalRequests = getStatValue(aggregatedMetrics,
                    "http.mainapp_sidecar_hcm_filter_(.*).downstream_rq_total");

            Long windowRequests = totalRequests - previousTotalRequests;

            return EnvoyStatData.builder()
                    .timestamp(coreMetric.timestamp())
                    .totalRequests(totalRequests)
                    .windowRequests(windowRequests)
                    .request2xx(getStatValue(aggregatedMetrics,
                            "http.mainapp_sidecar_hcm_filter_(.*).downstream_rq_2xx"))
                    .request3xx(getStatValue(aggregatedMetrics,
                            "http.mainapp_sidecar_hcm_filter_(.*).downstream_rq_3xx"))
                    .request4xx(getStatValue(aggregatedMetrics,
                            "http.mainapp_sidecar_hcm_filter_(.*).downstream_rq_4xx"))
                    .request5xx(getStatValue(aggregatedMetrics,
                            "http.mainapp_sidecar_hcm_filter_(.*).downstream_rq_5xx"))
                    .inactivityTime(
                            getInactivityTime(
                                    Optional.ofNullable(prevEnvoyStatData.getTimestamp()).orElse(coreMetric.timestamp()),
                                    coreMetric.timestamp(), windowRequests,
                                    Optional.ofNullable(prevEnvoyStatData.getInactivityTime()).orElse(0L))
                    ).build();

        }).orElse(prevEnvoyStatData);
    }

    private Long getStatValue(Map<String, Quantity> statMap, String statNamePattern) {
        return statMap.entrySet().stream()
                .filter(entry -> entry.getKey().matches(statNamePattern))
                .map(entry -> entry.getValue().getNumber().longValue()) // Extract the long value from
                // Quantity
                .findFirst()
                .orElse(0L);
    }


    // Calculate inactivity time
    public Long getInactivityTime(String previousTimestamp,
                                  String currentTimestamp,
                                  Long diffRequests,
                                  Long inactivityTime) {
        // If previous and current requests are 0, increase inactivity time
        if (diffRequests == 0) {

            ZonedDateTime current = ZonedDateTime.parse(currentTimestamp, DateTimeFormatter.ISO_ZONED_DATE_TIME);
            ZonedDateTime previous = ZonedDateTime.parse(previousTimestamp, DateTimeFormatter.ISO_ZONED_DATE_TIME);


            // Calculate time difference between previous and current timestamps
            Duration duration = Duration.between(previous, current);
            inactivityTime += duration.toSeconds();  // Accumulate inactivity time
        }
        // If current requests > 0, reset inactivity time
        else if (diffRequests > 0) {
            inactivityTime = 0L;
        }
        return inactivityTime;
    }
}
