package it.smartcommunitylabdhub.authorization.controllers;

import com.nimbusds.jose.jwk.JWKSet;
import it.smartcommunitylabdhub.authorization.components.JWKSetKeyStore;
import it.smartcommunitylabdhub.commons.config.SecurityProperties;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JWKSEndpoint {

    public static final String JWKS_URL = "/auth/jwks";

    @Autowired
    private JWKSetKeyStore jwkSetKeyStore;

    @Autowired
    private SecurityProperties securityProperties;

    @Value("${jwt.cache-control}")
    private String cacheControl;

    @GetMapping(JWKS_URL)
    public ResponseEntity<Map<String, Object>> getJWKInfo() {
        if (!securityProperties.isRequired()) {
            throw new UnsupportedOperationException();
        }

        //expose the entire jwkSet as JSON
        JWKSet jwkSet = jwkSetKeyStore.getJwkSet();
        Map<String, Object> jwkSetMap = jwkSet.toJSONObject();

        return ResponseEntity.ok().header(HttpHeaders.CACHE_CONTROL, cacheControl).body(jwkSetMap);
    }
}
