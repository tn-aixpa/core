package it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor;

import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1Pod;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.MonitorComponent;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sJobFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@ConditionalOnKubernetes
@Component
@MonitorComponent(framework = K8sJobFramework.FRAMEWORK)
public class K8sJobMonitor extends K8sBaseMonitor<K8sJobRunnable> {

    private final K8sJobFramework framework;

    public K8sJobMonitor(RunnableStore<K8sJobRunnable> runnableStore, K8sJobFramework k8sJobFramework) {
        super(runnableStore);
        Assert.notNull(k8sJobFramework, "job framework is required");

        this.framework = k8sJobFramework;
    }

    @Override
    public K8sJobRunnable refresh(K8sJobRunnable runnable) {
        try {
            V1Job job = framework.get(framework.build(runnable));

            if (job == null || job.getStatus() == null) {
                // something is missing, no recovery
                runnable.setState(State.ERROR.name());
            }

            log.info("Job status: {}", job.getStatus().toString());

            //TODO evaluate target for succeded/failed
            if (job.getStatus().getSucceeded() != null && job.getStatus().getSucceeded().intValue() > 0) {
                // Job has succeeded
                runnable.setState(State.COMPLETED.name());
            } else if (job.getStatus().getFailed() != null && job.getStatus().getFailed().intValue() > 0) {
                // Job has failed delete job and pod
                runnable.setState(State.ERROR.name());
            }

            //try to fetch pods
            List<V1Pod> pods = null;
            try {
                pods = framework.pods(job);
            } catch (K8sFrameworkException e1) {
                log.error("error collecting pods for job {}: {}", runnable.getId(), e1.getMessage());
            }

            //update results
            try {
                runnable.setResults(
                    Map.of(
                        "job",
                        mapper.convertValue(job, typeRef),
                        "pods",
                        pods != null ? mapper.convertValue(pods, arrayRef) : null
                    )
                );
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
