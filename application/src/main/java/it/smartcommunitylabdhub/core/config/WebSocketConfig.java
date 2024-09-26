package it.smartcommunitylabdhub.core.config;

import it.smartcommunitylabdhub.authorization.services.AuthorizableAwareEntityService;
import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import it.smartcommunitylabdhub.commons.config.SecurityProperties;
import it.smartcommunitylabdhub.commons.config.SecurityProperties.JwtAuthenticationProperties;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.core.websocket.UserNotificationService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.eclipse.jetty.http.HttpHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static final String WS_PATH = "/ws";

    //TODO completare configurazione (es. timeout)
    @Value("${security.api.cors.origins}")
    private String corsOrigins;

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    SecurityProperties securityProperties;

    @Autowired
    AuthorizableAwareEntityService<Project> projectAuthHelper;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //endpoint for a client to connect for the handshake
        StompWebSocketEndpointRegistration endpoint = registry.addEndpoint(WS_PATH);
        if (StringUtils.hasText(corsOrigins)) {
            Set<String> origins = StringUtils.commaDelimitedListToSet(corsOrigins);
            endpoint.setAllowedOrigins(origins.toArray(String[]::new));
        }
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //make sure messages are delivered in-order
        config.setPreservePublishOrder(true);

        //enable broker for calls and notifications
        config.enableSimpleBroker(SecurityConfig.API_PREFIX, UserNotificationService.PREFIX);
        config.setApplicationDestinationPrefixes(SecurityConfig.API_PREFIX);
        config.setUserDestinationPrefix(UserNotificationService.USER_PREFIX);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        if (securityProperties.isRequired()) {
            // auth converters
            List<AuthenticationConverter> converters = new ArrayList<>();
            List<AuthenticationProvider> providers = new ArrayList<>();

            if (securityProperties.isBasicAuthEnabled()) {
                BasicAuthenticationConverter basicAuthenticationConverter = new BasicAuthenticationConverter();
                converters.add(basicAuthenticationConverter);

                DaoAuthenticationProvider basicAuthProvider = new DaoAuthenticationProvider();
                basicAuthProvider.setUserDetailsService(
                    SecurityConfig.userDetailsService(
                        securityProperties.getBasic().getUsername(),
                        securityProperties.getBasic().getPassword()
                    )
                );
                providers.add(basicAuthProvider);
            }

            if (securityProperties.isJwtAuthEnabled()) {
                JwtAuthenticationProperties jwtProps = securityProperties.getJwt();
                AuthenticationConverter converter = new AuthenticationConverter() {
                    private final BearerTokenResolver resolver = new DefaultBearerTokenResolver();

                    @Override
                    public Authentication convert(HttpServletRequest request) {
                        String bearer = resolver.resolve(request);
                        if (bearer == null) {
                            return null;
                        }

                        return new BearerTokenAuthenticationToken(bearer);
                    }
                };
                converters.add(converter);

                JwtAuthenticationProvider provider = new JwtAuthenticationProvider(
                    SecurityConfig.externalJwtDecoder(jwtProps.getIssuerUri(), jwtProps.getAudience())
                );
                provider.setJwtAuthenticationConverter(
                    SecurityConfig.externalJwtAuthenticationConverter(
                        jwtProps.getUsername(),
                        jwtProps.getClaim(),
                        projectAuthHelper
                    )
                );

                providers.add(provider);
            }

            //TODO evaluate adding core token support

            AuthInterceptor authInterceptor = new AuthInterceptor(new ProviderManager(providers));
            authInterceptor.setConverters(converters);

            registration.interceptors(authInterceptor);
        }
    }

    public class AuthInterceptor implements ChannelInterceptor {

        private List<AuthenticationConverter> converters = Collections.emptyList();

        private final AuthenticationManager authManager;

        public AuthInterceptor(AuthenticationManager authManager) {
            Assert.notNull(authManager, "authentication manager can not be null");
            this.authManager = authManager;
        }

        public void setConverters(List<AuthenticationConverter> converters) {
            this.converters = converters;
        }

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

            //inject auth token on connect, will be propagated to ws session
            if (StompCommand.CONNECT == accessor.getCommand()) {
                // //figure out auth type, or anonymous
                // AbstractAuthenticationToken token = new AnonymousAuthenticationToken(
                //     "anonymous",
                //     "anonymous",
                //     Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                // );

                String header = accessor.getFirstNativeHeader(HttpHeader.AUTHORIZATION.asString());
                if (header != null) {
                    //mock request
                    MockHttpServletRequest request = new MockHttpServletRequest();
                    request.addHeader(HttpHeader.AUTHORIZATION.asString(), header);

                    //fetch token from converters
                    Authentication auth = converters
                        .stream()
                        .map(c -> c.convert(request))
                        .filter(t -> t != null)
                        .findFirst()
                        .orElse(null);

                    if (auth != null) {
                        //validate
                        Authentication authentication = authManager.authenticate(auth);

                        //inject
                        accessor.setHeader("simpUser", authentication);
                        accessor.setUser(authentication);
                    }
                    //     //basic
                    //     token = basicAuthenticationConverter.convert(request);

                    //     if (token == null) {
                    //         //bearer
                    //         String bearer = bearerTokenResolver.resolve(request);
                    //         if (bearer != null) {
                    //             token = new BearerTokenAuthenticationToken(bearer);
                    //         }
                    //     }
                }
                // accessor.setUser(token);

                // write
                // accessor.setHeader("simpUser", auth);
            }

            return message;
        }
    }
}
