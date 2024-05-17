package it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor;

import io.kubernetes.client.openapi.models.V1CronJob;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.MonitorComponent;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sCronJobFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sCronJobRunnable;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@ConditionalOnKubernetes
@Component
@MonitorComponent(framework = K8sCronJobFramework.FRAMEWORK)
public class K8sCronJobMonitor extends K8sBaseMonitor<K8sCronJobRunnable> {

    private final K8sCronJobFramework framework;

    public K8sCronJobMonitor(RunnableStore<K8sCronJobRunnable> runnableStore, K8sCronJobFramework k8sJobFramework) {
        super(runnableStore);
        Assert.notNull(k8sJobFramework, "cron job framework is required");

        this.framework = k8sJobFramework;
    }

    @Override
    public K8sCronJobRunnable refresh(K8sCronJobRunnable runnable) {
        try {
            V1CronJob job = framework.get(framework.build(runnable));

            if (job == null || job.getStatus() == null) {
                // something is missing, no recovery
                runnable.setState(State.ERROR.name());
            }

            log.info("Job status: {}", job.getStatus().toString());

            //TODO evaluate how to monitor
            //update results
            try {
                runnable.setResults(Map.of("cronJob", mapper.convertValue(job, typeRef)));
            } catch (IllegalArgumentException e) {
                log.error("error reading k8s results: {}", e.getMessage());
            }

            //collect logs, optional
            try {
                //TODO add sinceTime when available
                runnable.setLogs(framework.logs(job));
            } catch (K8sFrameworkException e1) {
                log.error("error collecting logs for job {}: {}", runnable.getId(), e1.getMessage());
            }
        } catch (K8sFrameworkException e) {
            // Set Runnable to ERROR state
            runnable.setState(State.ERROR.name());
        }

        return runnable;
    }
}
