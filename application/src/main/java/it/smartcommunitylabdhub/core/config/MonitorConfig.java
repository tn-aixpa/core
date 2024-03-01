package it.smartcommunitylabdhub.core.config;

import it.smartcommunitylabdhub.commons.annotations.infrastructure.MonitorComponent;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor.K8sBaseMonitor;
import it.smartcommunitylabdhub.fsm.pollers.PollingService;
import it.smartcommunitylabdhub.fsm.workflow.WorkflowFactory;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Configuration
@ConditionalOnKubernetes
public class MonitorConfig {

    public MonitorConfig(
        PollingService pollingService,
        List<K8sBaseMonitor<?>> k8sJobMonitors,
        @Value("${monitors.delay}") int delay,
        @Value("${monitors.min-delay}") int minDelay
    ) {
        Assert.isTrue(delay >= minDelay, "Delay must be greater than 0");

        k8sJobMonitors.forEach(monitor -> {
            Class<?> builderClass = monitor.getClass();
            if (builderClass.isAnnotationPresent(MonitorComponent.class)) {
                MonitorComponent annotation = builderClass.getAnnotation(MonitorComponent.class);

                pollingService.createPoller(
                    annotation.framework(),
                    WorkflowFactory.builder().step(i -> monitor.monitor()).build(),
                    delay,
                    true,
                    false
                );

                pollingService.startOne(annotation.framework());
            }
        });
    }
}
