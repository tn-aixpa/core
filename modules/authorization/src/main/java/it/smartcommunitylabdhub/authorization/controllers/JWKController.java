package it.smartcommunitylabdhub.authorization.controllers;




import com.nimbusds.jose.jwk.JWKSet;
import it.smartcommunitylabdhub.authorization.components.JWKSetKeyStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/.well-known/jwks.json")
@Slf4j
public class JWKController {

    @Autowired
    private JWKSetKeyStore jwkSetKeyStore;

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getJWKInfo() {
        JWKSet jwkSet = jwkSetKeyStore.getJwkSet();
        // Convert JWKSet to a map for easier JSON serialization
        Map<String, Object> jwkSetMap = jwkSet.toJSONObject();
        return ResponseEntity.ok(jwkSetMap);
    }
}

