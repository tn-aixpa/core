package it.smartcommunitylabdhub.framework.argo.config;

import io.kubernetes.client.openapi.ApiClient;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.argo.infrastructure.k8s.K8sArgoFramework;
import it.smartcommunitylabdhub.framework.argo.runnables.K8sArgoRunnable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class K8sArgoFrameworkConfig {

    @Bean
    @ConditionalOnKubernetes
    public RunnableStore<K8sArgoRunnable> k8sArgoRunnableStoreService(RunnableStore.StoreSupplier storeSupplier) {
        return storeSupplier.get(K8sArgoRunnable.class);
    }

    @Bean
    @ConditionalOnKubernetes
    public K8sArgoFramework k8sArgoFramework(ApiClient apiClient) {
        return new K8sArgoFramework(apiClient);
    }
}
