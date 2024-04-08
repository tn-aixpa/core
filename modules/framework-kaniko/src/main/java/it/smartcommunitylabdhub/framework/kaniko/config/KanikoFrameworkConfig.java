package it.smartcommunitylabdhub.framework.kaniko.config;

import io.kubernetes.client.openapi.ApiClient;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.kaniko.infrastructure.kaniko.K8sKanikoFramework;
import it.smartcommunitylabdhub.framework.kaniko.runnables.K8sKanikoRunnable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KanikoFrameworkConfig {


    @Bean
    @ConditionalOnKubernetes
    public RunnableStore<K8sKanikoRunnable> k8sKanikoRunnableStoreService(RunnableStore.StoreSupplier storeSupplier) {
        return storeSupplier.get(K8sKanikoRunnable.class);
    }


    @Bean
    @ConditionalOnKubernetes
    public K8sKanikoFramework k8sBuildFramework(ApiClient apiClient) {
        return new K8sKanikoFramework(apiClient);
    }
}
