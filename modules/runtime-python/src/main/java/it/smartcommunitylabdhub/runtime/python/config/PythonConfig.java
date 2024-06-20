package it.smartcommunitylabdhub.runtime.python.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PythonConfig {

    @Bean(name = "pythonImages")
    @ConfigurationProperties(prefix = "runtime.python.images")
    public Map<String, String> pythonImages() {
        return new HashMap<>();
    }
}
