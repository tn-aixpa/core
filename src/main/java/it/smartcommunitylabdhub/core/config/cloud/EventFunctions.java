package it.smartcommunitylabdhub.core.config.cloud;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class EventFunctions {

    @Bean
    public Consumer<String> logEntityEvent() {
        return entity -> log.info("Message received: {}", entity);
    }
}
