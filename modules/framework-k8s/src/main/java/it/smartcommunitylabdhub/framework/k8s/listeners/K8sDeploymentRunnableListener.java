package it.smartcommunitylabdhub.framework.k8s.listeners;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sDeploymentFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sDeploymentRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@ConditionalOnKubernetes
@Slf4j
public class K8sDeploymentRunnableListener {

    @Autowired
    K8sDeploymentFramework k8sDeployFramework;

    @Autowired
    private RunnableStore<K8sDeploymentRunnable> runnableStore;

    @Async
    @EventListener
    public void listen(K8sDeploymentRunnable runnable) {
        Assert.notNull(runnable, "runnable can not be null");
        Assert.hasText(runnable.getId(), "runnable id can not be null or empty");

        log.info("Receive runnable for execution: {}", runnable.getId());

        try {
            //store runnable
            runnableStore.store(runnable.getId(), runnable);

            try {
                log.debug("Execute runnable {} via framework", runnable.getId());
                runnable = k8sDeployFramework.execute(runnable);

                log.debug("Update runnable {} via framework", runnable.getId());
                runnableStore.store(runnable.getId(), runnable);
                //TODO send run event to RunManager(todo) create an object of type RunState with stateId, runId, project, framework....
            } catch (K8sFrameworkException e) {
                log.error("Error with k8s: {}", e.getMessage());
            } finally {
                //remove after execution
                //TODO, needs to cleanup FSM usage in framework
                log.debug("Completed runnable {}", runnable.getId());
            }
        } catch (StoreException e) {
            log.error("error with runnable store: {}", e.getMessage());
        }
    }
}
