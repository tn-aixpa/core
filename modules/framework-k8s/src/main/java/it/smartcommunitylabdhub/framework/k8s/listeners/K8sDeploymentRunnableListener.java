package it.smartcommunitylabdhub.framework.k8s.listeners;

import it.smartcommunitylabdhub.commons.services.RunnableStoreService;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.K8sDeploymentFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sDeploymentRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnKubernetes
public class K8sDeploymentRunnableListener {

    @Autowired
    K8sDeploymentFramework k8sDeployFramework;

    @Autowired
    private RunnableStoreService<K8sDeploymentRunnable> myRunnableStoreService;

    @Async
    @EventListener
    public void listen(K8sDeploymentRunnable runnable) {
        myRunnableStoreService.store(runnable.getId(), runnable);

        k8sDeployFramework.execute(runnable);
    }
}
