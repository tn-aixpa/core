package it.smartcommunitylabdhub.framework.k8s.listeners;

import it.smartcommunitylabdhub.commons.services.RunnableStoreService;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.K8sServeFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sServeRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnKubernetes
public class K8sServeRunnableListener {

    @Autowired
    K8sServeFramework k8sServeFramework;

    @Autowired
    private RunnableStoreService<K8sServeRunnable> myRunnableStoreService;

    @Async
    @EventListener
    public void listen(K8sServeRunnable runnable) {
        myRunnableStoreService.store(runnable.getId(), runnable);

        k8sServeFramework.execute(runnable);
    }
}
