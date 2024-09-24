package it.smartcommunitylabdhub.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
// @EnableWebSocketSecurity
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(final MessageSecurityMetadataSourceRegistry messages) {
        //TODO migrate to webSocketSecurity with disabled CORS when possible
        messages.simpTypeMatchers(SimpMessageType.DISCONNECT).permitAll().anyMessage().authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
        //disable csrf
        return true;
    }
}
