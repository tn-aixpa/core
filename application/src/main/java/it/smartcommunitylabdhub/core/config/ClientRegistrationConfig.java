package it.smartcommunitylabdhub.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.List;
@Configuration
public class ClientRegistrationConfig {

    @Value("${security.oidc.client-id}")
    private String clientId;

    @Value("${security.oidc.issuer-uri}")
    private String issuerUri;

    @Value("${security.oidc.scope}")
    private List<String> scope;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(getGoogleClientRegistration());
    }

    private ClientRegistration getGoogleClientRegistration() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("core")
                .clientId(clientId)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/core")
                .scope(scope)
                .authorizationUri("https://aac.digitalhub-dev.smartcommunitylab.it/oauth/authorize")
                .tokenUri("https://aac.digitalhub-dev.smartcommunitylab.it/oauth/token")
                .clientName("Core")
                .build();

        return clientRegistration;
    }

}