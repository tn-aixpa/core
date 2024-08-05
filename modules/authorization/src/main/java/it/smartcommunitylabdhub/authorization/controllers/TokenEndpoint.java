package it.smartcommunitylabdhub.authorization.controllers;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import it.smartcommunitylabdhub.authorization.components.JWKSetKeyStore;
import it.smartcommunitylabdhub.authorization.model.TokenResponse;
import it.smartcommunitylabdhub.authorization.services.JwtTokenService;
import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenEndpoint implements InitializingBean {

    public static final String TOKEN_URL = "/auth/token";

    @Value("${jwt.client-id}")
    private String clientId;

    @Value("${jwt.client-secret}")
    private String clientSecret;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private JWKSetKeyStore jwkSetKeyStore;

    @Autowired
    private ApplicationProperties applicationProperties;

    private JwtAuthenticationProvider authProvider;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(jwkSetKeyStore, "jwks store is required");
        Assert.notNull(jwkSetKeyStore.getJwk(), "jwk is required");

        //build auth provider to validate tokens
        authProvider = new JwtAuthenticationProvider(coreJwtDecoder(jwkSetKeyStore.getJwk()));
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("authorities");
        authoritiesConverter.setAuthorityPrefix("");
        jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        authProvider.setJwtAuthenticationConverter(jwtConverter);
    }

    @PostMapping(TOKEN_URL)
    public TokenResponse token(@RequestParam Map<String, String> parameters, Authentication authentication) {
        //resolve client authentication
        if (authentication == null || !(authentication.isAuthenticated())) {
            throw new InsufficientAuthenticationException("Invalid or missing authentication");
        }

        //select flow
        String grantType = parameters.get(OAuth2ParameterNames.GRANT_TYPE);

        if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(grantType)) {
            return clientCredentials(parameters, authentication);
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(grantType)) {
            return refreshToken(parameters, authentication);
        }

        throw new IllegalArgumentException("invalid or unsupported grant type");
    }

    private TokenResponse refreshToken(Map<String, String> parameters, Authentication authentication) {
        //refresh token is usable without credentials
        //TODO add rotation by storing refresh tokens in db!

        String grantType = parameters.get(OAuth2ParameterNames.GRANT_TYPE);
        if (!AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(grantType)) {
            throw new IllegalArgumentException("invalid grant type");
        }

        String token = parameters.get("refresh_token");
        if (token == null) {
            throw new IllegalArgumentException("invalid or missing refresh_token");
        }

        //validate via provider
        try {
            BearerTokenAuthenticationToken request = new BearerTokenAuthenticationToken(token);
            Authentication auth = authProvider.authenticate(request);
            if (!auth.isAuthenticated()) {
                throw new IllegalArgumentException("invalid or missing refresh_token");
            }

            //token is valid, use as context for generation
            return jwtTokenService.generateCredentials(auth);
        } catch (AuthenticationException ae) {
            throw new IllegalArgumentException("invalid or missing refresh_token");
        }
    }

    private TokenResponse clientCredentials(Map<String, String> parameters, Authentication authentication) {
        //client credentials *requires* basic auth
        if (authentication == null || !(authentication instanceof UsernamePasswordAuthenticationToken)) {
            throw new InsufficientAuthenticationException("Invalid or missing authentication");
        }

        //for client credentials to mimic admin user client *must* match authenticated user
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
        if (clientId != null && !clientId.equals(auth.getName())) {
            throw new InsufficientAuthenticationException("Invalid client authentication");
        }

        String grantType = parameters.get(OAuth2ParameterNames.GRANT_TYPE);
        if (!AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(grantType)) {
            throw new IllegalArgumentException("invalid grant type");
        }

        //generate as per user
        return jwtTokenService.generateCredentials(authentication);
    }

    private JwtDecoder coreJwtDecoder(JWK jwk) throws JOSEException {
        //we support only RSA keys
        if (!(jwk instanceof RSAKey)) {
            throw new IllegalArgumentException("the provided key is not suitable for token authentication");
        }

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(jwk.toRSAKey().toRSAPublicKey()).build();

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(
            applicationProperties.getEndpoint()
        );
        OAuth2TokenValidator<Jwt> audienceValidator = new JwtClaimValidator<List<String>>(
            JwtClaimNames.AUD,
            (aud -> aud != null && aud.contains(applicationProperties.getName()))
        );

        //refresh tokens *must contain* at_hash
        OAuth2TokenValidator<Jwt> tokenValidator = new JwtClaimValidator<String>(
            IdTokenClaimNames.AT_HASH,
            (hash -> hash != null)
        );

        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
            withIssuer,
            audienceValidator,
            tokenValidator
        );
        jwtDecoder.setJwtValidator(validator);

        return jwtDecoder;
    }
}
