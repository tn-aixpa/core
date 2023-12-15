package it.smartcommunitylabdhub.core.config.queue;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitAmqpConfig {

    @Bean
    public Queue dhcoreQueue() {
        return new Queue("dhcore_queue");
    }
}
