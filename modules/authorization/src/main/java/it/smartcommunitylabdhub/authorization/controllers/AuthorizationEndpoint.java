package it.smartcommunitylabdhub.authorization.controllers;


import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class AuthorizationEndpoint {


    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/oauth2/login")
    public ResponseEntity<Authentication> login(Authentication authentication) {
        return ResponseEntity.ok(authentication);
    }
}
