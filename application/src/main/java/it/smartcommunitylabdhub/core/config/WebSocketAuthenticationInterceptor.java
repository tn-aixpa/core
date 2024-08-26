package it.smartcommunitylabdhub.core.config;

import java.util.Collections;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@EnableWebSocketMessageBroker
public class WebSocketAuthenticationInterceptor implements WebSocketMessageBrokerConfigurer {

    //TODO completare autenticazione
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
            new ChannelInterceptor() {
                @Override
                public Message<?> preSend(Message<?> message, MessageChannel channel) {
                    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                    );
                    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                        String authHeader = accessor.getFirstNativeHeader("Authorization"); //TODO use to create JwtAuthenticationToken for oidc?
                        String userHeader = accessor.getFirstNativeHeader("user");
                        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(
                            userHeader,
                            null,
                            Collections.singleton((GrantedAuthority) () -> "ROLE_USER")
                        );
                        accessor.setUser(user);
                    }
                    return message;
                }
            }
        );
    }
}
