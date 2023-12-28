package it.smartcommunitylabdhub.core.components.cloud.processors;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@Component
public class EntityProcessor {
    @Bean
    public Function<String, String> serializeEntity() {
        return (value) -> value;
    }
}
