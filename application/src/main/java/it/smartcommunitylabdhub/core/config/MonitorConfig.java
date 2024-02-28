package it.smartcommunitylabdhub.core.config;

import it.smartcommunitylabdhub.commons.annotations.infrastructure.MonitorComponent;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor.K8sBaseMonitor;
import it.smartcommunitylabdhub.fsm.pollers.PollingService;
import it.smartcommunitylabdhub.fsm.workflow.WorkflowFactory;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnKubernetes
public class MonitorConfig {

    @Value("${monitors.delay}")
    private int delay;

    public MonitorConfig(PollingService pollingService, List<K8sBaseMonitor<?>> k8sJobMonitors) {
        k8sJobMonitors.forEach(monitor -> {
            Class<?> builderClass = monitor.getClass();
            if (builderClass.isAnnotationPresent(MonitorComponent.class)) {
                MonitorComponent annotation = builderClass.getAnnotation(MonitorComponent.class);

                pollingService.createPoller(
                    annotation.framework(),
                    WorkflowFactory.builder().step(i -> monitor.monitor()).build(),
                    5,
                    true,
                    false
                );

                pollingService.startOne(annotation.framework());
            }
        });
    }
}
