package it.smartcommunitylabdhub.framework.k8s.listeners;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sJobFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@ConditionalOnKubernetes
@Slf4j
public class K8sJobRunnableListener {

    @Autowired
    K8sJobFramework k8sJobFramework;

    @Autowired
    private RunnableStore<K8sJobRunnable> runnableStore;

    @Async
    @EventListener
    public void listen(K8sJobRunnable runnable) {
        //TODO
        // check if ready call execute..
        // if stop call stop.
        Assert.notNull(runnable, "runnable can not be null");
        Assert.hasText(runnable.getId(), "runnable id can not be null or empty");

        log.info("Receive runnable for execution: {}", runnable.getId());

        try {
            // Read action from runnable
            // if run execute
            // if stop stop
            ///
            //...

            /// store runnable

            //store runnable
            runnableStore.store(runnable.getId(), runnable);

            // if READY call execute else STOP(future)
            try {
                log.debug("Execute runnable {} via framework", runnable.getId());
                runnable = k8sJobFramework.run(runnable);

                log.debug("Update runnable {} via framework", runnable.getId());
                runnableStore.store(runnable.getId(), runnable);
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
