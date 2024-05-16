package it.smartcommunitylabdhub.framework.kaniko.infrastructure.monitor;

import io.kubernetes.client.openapi.models.V1Job;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.MonitorComponent;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor.K8sBaseMonitor;
import it.smartcommunitylabdhub.framework.kaniko.infrastructure.k8s.K8sKanikoFramework;
import it.smartcommunitylabdhub.framework.kaniko.runnables.K8sKanikoRunnable;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@ConditionalOnKubernetes
@Component
@MonitorComponent(framework = "build")
public class K8sKanikoMonitor extends K8sBaseMonitor<K8sKanikoRunnable> {

    private final K8sKanikoFramework framework;

    public K8sKanikoMonitor(RunnableStore<K8sKanikoRunnable> runnableStore, K8sKanikoFramework kanikoFramework) {
        super(runnableStore);
        Assert.notNull(kanikoFramework, "kaniko framework is required");

        this.framework = kanikoFramework;
    }

    @Override
    public K8sKanikoRunnable refresh(K8sKanikoRunnable runnable) {
        try {
            V1Job job = framework.get(framework.build(runnable));

            if (job == null || job.getStatus() == null) {
                // something is missing, no recovery
                runnable.setState(State.ERROR.name());
            }

            log.info("Job status: {}", job.getStatus().toString());

            //target for succeded/failed is 1
            if (job.getStatus().getSucceeded() != null && job.getStatus().getSucceeded().intValue() > 0) {
                // Job has succeeded
                runnable.setState(State.COMPLETED.name());
            } else if (job.getStatus().getFailed() != null && job.getStatus().getSucceeded().intValue() > 0) {
                // Job has failed delete job and pod
                runnable.setState(State.ERROR.name());
            }

            //update results
            try {
                runnable.setResults(Map.of("job", mapper.convertValue(job, typeRef)));
            } catch (IllegalArgumentException e) {
                log.error("error reading k8s results: {}", e.getMessage());
            }

            //collect logs, optional
            try {
                //TODO add sinceTime when available
                //TODO override base logs to collect from kaniko because we inject a sidecar/init
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
