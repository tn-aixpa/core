package it.smartcommunitylabdhub.core.config;


import io.kubernetes.client.openapi.ApiClient;
import it.smartcommunitylabdhub.core.components.infrastructure.frameworks.K8sDeploymentFramework;
import it.smartcommunitylabdhub.core.components.infrastructure.frameworks.K8sJobFramework;
import it.smartcommunitylabdhub.core.components.infrastructure.frameworks.K8sServeFramework;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class FrameworkConfig {

    @Bean
//    @ConditionalOnBean(ApiClient.class)
    K8sServeFramework k8sServeFramework(ApiClient apiClient) {
        return new K8sServeFramework(apiClient);
    }

    @Bean
//    @ConditionalOnBean(ApiClient.class)
    K8sJobFramework k8sJobFramework(ApiClient apiClient) {
        return new K8sJobFramework(apiClient);
    }

    @Bean
//    @ConditionalOnBean(ApiClient.class)
    K8sDeploymentFramework k8sDeploymentFramework(ApiClient apiClient) {
        return new K8sDeploymentFramework(apiClient);
    }
}
