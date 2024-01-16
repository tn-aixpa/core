package it.smartcommunitylabdhub.core.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@ConditionalOnProperty(name = "event-queue.services.rabbit.enabled", havingValue = "true", matchIfMissing = false)
@Import(RabbitAutoConfiguration.class)
public class RabbitMQConfig {

    @Bean
    public Queue myQueue() {
        return new Queue("dhCoreQueue", true); // Set the durable option here
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("entityTopic", true, false);
    }

    @Bean
    public Binding binding(Queue myQueue, TopicExchange topicExchange) {
        //return BindingBuilder.bind(myQueue).to(myExchange).with("myRoutingKey");
        return BindingBuilder.bind(myQueue).to(topicExchange).with("entityRoutingKey");
    }


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("192.168.49.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");

        return connectionFactory;
    }

}
