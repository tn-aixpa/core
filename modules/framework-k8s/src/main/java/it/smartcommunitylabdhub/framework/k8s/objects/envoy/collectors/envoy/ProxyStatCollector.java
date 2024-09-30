package it.smartcommunitylabdhub.framework.k8s.objects.envoy.collectors.envoy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
                EnvoyStatData prevEnvoyStatData = mapper.convertValue(
                                run.getStatus().getOrDefault("proxy", new HashMap<>()), EnvoyStatData.class);

                Map<String, Quantity> aggregatedMetrics = coreMetricsList.stream()
                                .flatMap(coreMetric -> coreMetric.metrics().stream())
                                .flatMap(containerMetric -> containerMetric.getUsage().entrySet().stream())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));

                Long prevTotal = Optional.ofNullable(prevEnvoyStatData.getTotalRequests()).orElse(Long.valueOf(0));
                Long currentTotal = getStatValue(aggregatedMetrics,
                                "http.mainapp_sidecar_hcm_filter_(.*).downstream_rq_total");
                return new EnvoyStatData(
                                currentTotal,
                                calculateRequestsInRange(currentTotal, prevTotal),
                                getStatValue(aggregatedMetrics,
                                                "http.mainapp_sidecar_hcm_filter_(.*).downstream_rq_2xx"),
                                getStatValue(aggregatedMetrics,
                                                "http.mainapp_sidecar_hcm_filter_(.*).downstream_rq_3xx"),
                                getStatValue(aggregatedMetrics,
                                                "http.mainapp_sidecar_hcm_filter_(.*).downstream_rq_4xx"),
                                getStatValue(aggregatedMetrics,
                                                "http.mainapp_sidecar_hcm_filter_(.*).downstream_rq_5xx"));
        }

        private Long getStatValue(Map<String, Quantity> statMap, String statNamePattern) {
                return statMap.entrySet().stream()
                                .filter(entry -> entry.getKey().matches(statNamePattern))
                                .map(entry -> entry.getValue().getNumber().longValue()) // Extract the long value from
                                                                                        // Quantity
                                .findFirst()
                                .orElse(0L);
        }

        private Long calculateRequestsInRange(long currentTotal, long previousTotal) {
                return currentTotal - previousTotal;
        }
}
