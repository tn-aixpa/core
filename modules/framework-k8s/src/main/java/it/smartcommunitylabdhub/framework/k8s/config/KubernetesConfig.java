package it.smartcommunitylabdhub.framework.k8s.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import it.smartcommunitylabdhub.commons.services.RunnableStoreService;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sDeploymentRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sServeRunnable;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KubernetesConfig {

    @Autowired
    protected RunnableStoreService.StoreSupplier storeSupplier;

    @Bean
    @ConditionalOnCloudPlatform(CloudPlatform.KUBERNETES)
    public RunnableStoreService<K8sServeRunnable> k8sServeRunnableStoreService() {
        return storeSupplier.get(K8sServeRunnable.class);
    }

    @Bean
    @ConditionalOnCloudPlatform(CloudPlatform.KUBERNETES)
    public RunnableStoreService<K8sDeploymentRunnable> deploymentRunnableStoreService() {
        return storeSupplier.get(K8sDeploymentRunnable.class);
    }

    @Bean
    @ConditionalOnCloudPlatform(CloudPlatform.KUBERNETES)
    public RunnableStoreService<K8sJobRunnable> runnableStoreService() {
        return storeSupplier.get(K8sJobRunnable.class);
    }

    @Bean
    @ConditionalOnCloudPlatform(CloudPlatform.KUBERNETES)
    public ApiClient kubeApiClusterClient() {
        try {
            return ClientBuilder.cluster().build();
        } catch (IOException e1) {
            throw new UnsupportedOperationException("Could not initialize connection to kubernetes.");
        }
    }

    @Bean
    @ConditionalOnMissingBean(ApiClient.class)
    @ConditionalOnKubernetes
    public ApiClient kubeApiStandardClient() {
        try {
            return ClientBuilder.standard().build();
        } catch (IOException e1) {
            throw new UnsupportedOperationException("Could not initialize connection to kubernetes.");
        }
    }
}
