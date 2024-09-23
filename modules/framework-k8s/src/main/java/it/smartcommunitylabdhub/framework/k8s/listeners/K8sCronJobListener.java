package it.smartcommunitylabdhub.framework.k8s.listeners;

import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sCronJobFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sCronJobRunnable;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

public class K8sCronJobListener extends K8sRunnableListener<K8sCronJobRunnable> {

    public K8sCronJobListener(K8sCronJobFramework k8sFramework, RunnableStore<K8sCronJobRunnable> runnableStore) {
        super(k8sFramework, runnableStore);
    }

    @Async
    @EventListener
    public void listen(K8sCronJobRunnable runnable) {
        if (runnable != null) {
            //clone to fully detach
            process(runnable.toBuilder().build());
        }
    }
}
