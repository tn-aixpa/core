package it.smartcommunitylabdhub.core.config.cloud;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue myQueue() {
        return new Queue("dh_core");
    }
}
