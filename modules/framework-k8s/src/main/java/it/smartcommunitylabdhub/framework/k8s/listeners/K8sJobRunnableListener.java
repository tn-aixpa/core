package it.smartcommunitylabdhub.framework.k8s.listeners;

import it.smartcommunitylabdhub.commons.services.RunnableStoreService;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.K8sJobFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnKubernetes
public class K8sJobRunnableListener {

    @Autowired
    K8sJobFramework k8sJobFramework;

    @Autowired
    private RunnableStoreService<K8sJobRunnable> myRunnableStoreService;

    @Async
    @EventListener
    public void listen(K8sJobRunnable runnable) {
        myRunnableStoreService.store(runnable.getId(), runnable);

        k8sJobFramework.execute(runnable);
    }
}
