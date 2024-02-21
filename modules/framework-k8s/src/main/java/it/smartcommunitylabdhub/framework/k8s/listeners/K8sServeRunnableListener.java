package it.smartcommunitylabdhub.framework.k8s.listeners;

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

    //    private final Supplier<RunnableStoreService<K8sServeRunnable>> myRunnableSupplier;

    @Autowired
    K8sServeFramework k8sServeFramework;

    //    public K8sServeRunnableListener(@RunnableSupplier Supplier<RunnableStoreService<K8sServeRunnable>> myRunnableSupplier) {
    //        this.myRunnableSupplier = myRunnableSupplier;
    //    }

    @Async
    @EventListener
    public void listen(K8sServeRunnable runnable) {
        k8sServeFramework.execute(runnable);
    }
}
