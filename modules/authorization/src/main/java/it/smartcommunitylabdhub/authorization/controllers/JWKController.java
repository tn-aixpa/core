package it.smartcommunitylabdhub.authorization.controllers;

import com.nimbusds.jose.jwk.JWKSet;
import it.smartcommunitylabdhub.authorization.components.JWKSetKeyStore;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class JWKController {

    @Autowired
    private JWKSetKeyStore jwkSetKeyStore;

    @Value("${jwks.cache-control}")
    private String cacheControl;

    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Map<String, Object>> getJWKInfo() {
        JWKSet jwkSet = jwkSetKeyStore.getJwkSet();
        // Convert JWKSet to a map for easier JSON serialization
        Map<String, Object> jwkSetMap = jwkSet.toJSONObject();

        return ResponseEntity.ok().header(HttpHeaders.CACHE_CONTROL, cacheControl).body(jwkSetMap);
    }
}
