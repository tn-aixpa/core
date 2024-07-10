package it.smartcommunitylabdhub.framework.k8s.listeners;

import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sJobFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

public class K8sJobListener extends K8sRunnableListener<K8sJobRunnable> {

    public K8sJobListener(K8sJobFramework k8sFramework, RunnableStore<K8sJobRunnable> runnableStore) {
        super(k8sFramework, runnableStore);
    }

    @Async
    @EventListener
    public void listen(K8sJobRunnable runnable) {
        process(runnable);
    }
}
