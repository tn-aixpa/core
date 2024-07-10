package it.smartcommunitylabdhub.framework.k8s.listeners;

import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sServeFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sServeRunnable;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

public class K8sServeListener extends K8sRunnableListener<K8sServeRunnable> {

    public K8sServeListener(K8sServeFramework k8sFramework, RunnableStore<K8sServeRunnable> runnableStore) {
        super(k8sFramework, runnableStore);
    }

    @Async
    @EventListener
    public void listen(K8sServeRunnable runnable) {
        process(runnable);
    }
}
