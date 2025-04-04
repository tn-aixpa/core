package it.smartcommunitylabdhub.framework.k8s.config;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnKubernetes
public class K8sFabric8ClientConfig {

    @Bean
    public KubernetesClient kubernetesClient() {
        return new KubernetesClientBuilder().build();
    }
}
