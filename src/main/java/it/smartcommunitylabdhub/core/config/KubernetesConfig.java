package it.smartcommunitylabdhub.core.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.function.Supplier;

@Configuration
public class KubernetesConfig {
    @Bean
    Supplier<ApiClient> kubeApiClientSupplier() {
        return () -> {
            try {
                try {
                    return ClientBuilder.standard().build();
                } catch (IOException e) {
                    return ClientBuilder.cluster().build();
                }
            } catch (Exception e1) {
                return null;
            }
        };
    }

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
