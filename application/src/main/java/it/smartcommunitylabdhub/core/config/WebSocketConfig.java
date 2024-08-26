package it.smartcommunitylabdhub.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //TODO completare configurazione (es. timeout)

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/mywebsocket").setAllowedOrigins("*"); //endpoint for a client to connect for the handshake
    }

    @Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// config.setApplicationDestinationPrefixes("/app");
		config.enableSimpleBroker("/runs"); //endpoint for a client to subscribe to (e.g. /topic/runs)
	}
}
