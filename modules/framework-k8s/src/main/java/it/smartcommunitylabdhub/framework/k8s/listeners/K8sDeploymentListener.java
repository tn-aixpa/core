package it.smartcommunitylabdhub.framework.k8s.listeners;

import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sDeploymentFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sDeploymentRunnable;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

public class K8sDeploymentListener extends K8sRunnableListener<K8sDeploymentRunnable> {

    public K8sDeploymentListener(
        K8sDeploymentFramework k8sFramework,
        RunnableStore<K8sDeploymentRunnable> runnableStore
    ) {
        super(k8sFramework, runnableStore);
    }

    @Async
    @EventListener
    public void listen(K8sDeploymentRunnable runnable) {
        if (runnable != null) {
            //clone to fully detach
            process(runnable.toBuilder().build());
        }
    }
}
