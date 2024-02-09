package it.smartcommunitylabdhub.framework.k8s.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import java.io.IOException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KubernetesConfig {

  @Bean
  @ConditionalOnCloudPlatform(CloudPlatform.KUBERNETES)
  public ApiClient kubeApiClusterClient() {
    try {
      return ClientBuilder.cluster().build();
    } catch (IOException e1) {
      throw new UnsupportedOperationException(
        "Could not initialize connection to kubernetes."
      );
    }
  }

  @Bean
  @ConditionalOnMissingBean(ApiClient.class)
  @ConditionalOnKubernetes
  public ApiClient kubeApiStandardClient() {
    try {
      return ClientBuilder.standard().build();
    } catch (IOException e1) {
      throw new UnsupportedOperationException(
        "Could not initialize connection to kubernetes."
      );
    }
  }
}
