package it.smartcommunitylabdhub.core.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

@Configuration
public class KubernetesConfig {

  @Bean
  @Nullable
  ApiClient kubeApiClient() {
    try {
      try {
        return ClientBuilder.standard().build();
      } catch (IOException e) {
        return ClientBuilder.cluster().build();
      }
    } catch (Exception e1) {
      return null;
    }
  }
}
