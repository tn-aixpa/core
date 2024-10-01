package it.smartcommunitylabdhub.framework.k8s.processors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylabdhub.commons.annotations.common.RunProcessorType;
import it.smartcommunitylabdhub.commons.infrastructure.RunProcessor;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import it.smartcommunitylabdhub.framework.k8s.model.K8sProxyStatus;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreMetric;
import it.smartcommunitylabdhub.framework.k8s.objects.envoy.collectors.envoy.ProxyStatCollector;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sServeRunnable;
import lombok.extern.slf4j.Slf4j;

@RunProcessorType(stages = { "onRunning", "onCompleted", "onError", "onStopped",
        "onDeleted" }, id = K8sProxyProcessor.ID)
@Component(K8sProxyProcessor.ID)
@Slf4j
public class K8sProxyProcessor implements RunProcessor<RunBaseStatus> {

    public static final String ID = "k8sProxyProcessor";

    private final ProxyStatCollector envoyStatCollector;

    private final ObjectMapper mapper = new ObjectMapper();

    public K8sProxyProcessor(ProxyStatCollector envoyStatCollector) {
        this.envoyStatCollector = envoyStatCollector;
    }

    @Override
    public RunBaseStatus process(Run run, RunRunnable runRunnable, RunBaseStatus status) {
        if (runRunnable instanceof K8sRunnable) {
            // extract logs
            List<CoreMetric> metrics = ((K8sServeRunnable) runRunnable).getMetrics();

            if (metrics != null) {
                try {
                    String envoyStats = mapper.writeValueAsString(
                            envoyStatCollector.collectEnvoyStats(metrics, run));
                    log.info(envoyStats);

                    Map<String, Serializable> proxyStatus = mapper.readValue(envoyStats,
                            new TypeReference<HashMap<String, Serializable>>() {
                            });

                    return K8sProxyStatus.builder().proxy(proxyStatus).build();

                } catch (JsonProcessingException e) {
                    // Log error or leave return null
                    return null;
                }
            }
        }

        return null;
    }

}
