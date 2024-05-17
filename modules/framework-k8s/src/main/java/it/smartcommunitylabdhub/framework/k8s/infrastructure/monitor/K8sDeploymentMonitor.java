package it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor;

import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Pod;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.MonitorComponent;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sDeploymentFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sDeploymentRunnable;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@ConditionalOnKubernetes
@Component
@MonitorComponent(framework = K8sDeploymentFramework.FRAMEWORK)
public class K8sDeploymentMonitor extends K8sBaseMonitor<K8sDeploymentRunnable> {

    private final K8sDeploymentFramework framework;

    public K8sDeploymentMonitor(
        RunnableStore<K8sDeploymentRunnable> runnableStore,
        K8sDeploymentFramework deploymentFramework
    ) {
        super(runnableStore);
        Assert.notNull(deploymentFramework, "deployment framework is required");

        this.framework = deploymentFramework;
    }

    @Override
    public K8sDeploymentRunnable refresh(K8sDeploymentRunnable runnable) {
        try {
            V1Deployment deployment = framework.get(framework.build(runnable));

            // check status
            // if ERROR signal, otherwise let RUNNING
            if (deployment == null || deployment.getStatus() == null) {
                // something is missing, no recovery
                runnable.setState(State.ERROR.name());
            }

            log.debug("deployment status: replicas {}", deployment.getStatus().getReadyReplicas());

            //try to fetch pods
            List<V1Pod> pods = null;
            try {
                pods = framework.pods(deployment);
            } catch (K8sFrameworkException e1) {
                log.error("error collecting pods for deployment {}: {}", runnable.getId(), e1.getMessage());
            }

            //update results
            try {
                runnable.setResults(
                    Map.of(
                        "deployment",
                        mapper.convertValue(deployment, typeRef),
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
                runnable.setLogs(framework.logs(deployment));
            } catch (K8sFrameworkException e1) {
                log.error("error collecting logs for {}: {}", runnable.getId(), e1.getMessage());
            }
            
            //collect metrics, optional
            try {
                runnable.setMetrics(framework.metrics(deployment));
            } catch (K8sFrameworkException e1) {
                log.error("error collecting metrics for {}: {}", runnable.getId(), e1.getMessage());
            }
        } catch (K8sFrameworkException e) {
            // Set Runnable to ERROR state
            runnable.setState(State.ERROR.name());
        }

        return runnable;
    }
}
