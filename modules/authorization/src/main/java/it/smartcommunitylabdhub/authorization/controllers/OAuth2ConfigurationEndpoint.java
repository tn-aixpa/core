package it.smartcommunitylabdhub.authorization.controllers;

import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import it.smartcommunitylabdhub.commons.config.SecurityProperties;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuth2ConfigurationEndpoint {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private SecurityProperties securityProperties;

    @Value("${jwt.cache-control}")
    private String cacheControl;

    private Map<String, Object> config = null;

    @GetMapping(value = { "/.well-known/openid-configuration", "/.well-known/oauth-authorization-server" })
    public ResponseEntity<Map<String, Object>> getCOnfiguration() {
        if (!securityProperties.isRequired()) {
            throw new UnsupportedOperationException();
        }

        if (config == null) {
            config = generate();
        }

        return ResponseEntity.ok().header(HttpHeaders.CACHE_CONTROL, cacheControl).body(config);
    }

    private Map<String, Object> generate() {
        /*
         * OpenID Provider Metadata
         * https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata
         */

        String baseUrl = applicationProperties.getEndpoint();
        Map<String, Object> m = new HashMap<>();

        m.put("issuer", baseUrl);
        m.put("jwks_uri", baseUrl + JWKSEndpoint.JWKS_URL);
        m.put("response_types_supported", Collections.emptyList());

        List<String> grantTypes = Stream
            .of(AuthorizationGrantType.CLIENT_CREDENTIALS, AuthorizationGrantType.REFRESH_TOKEN)
            .map(t -> t.getValue())
            .toList();
        m.put("grant_types_supported", grantTypes);

        m.put("token_endpoint", baseUrl + TokenEndpoint.TOKEN_URL);
        List<String> authMethods = Collections.singletonList("client_secret_basic");
        m.put("token_endpoint_auth_methods_supported", authMethods);

        return m;
    }
}
