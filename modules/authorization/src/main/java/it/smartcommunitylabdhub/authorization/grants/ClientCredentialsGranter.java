/**
 * Copyright 2025 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.smartcommunitylabdhub.authorization.grants;

import it.smartcommunitylabdhub.authorization.model.TokenResponse;
import it.smartcommunitylabdhub.authorization.model.UserAuthentication;
import it.smartcommunitylabdhub.authorization.services.TokenService;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class ClientCredentialsGranter implements TokenGranter {

    private String clientId;

    private final TokenService tokenService;

    public ClientCredentialsGranter(TokenService jwtTokenService) {
        Assert.notNull(jwtTokenService, "token service is required");
        this.tokenService = jwtTokenService;
    }

    @Autowired
    public void setClientId(@Value("${jwt.client-id}") String clientId) {
        this.clientId = clientId;
    }

    @Override
    public TokenResponse grant(@NotNull Map<String, String> parameters, Authentication authentication) {
        //client credentials *requires* basic auth
        if (authentication == null || !(authentication instanceof UsernamePasswordAuthenticationToken)) {
            throw new InsufficientAuthenticationException("Invalid or missing authentication");
        }

        //for client credentials to mimic admin user client *must* match authenticated user
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
        if (clientId != null && !clientId.equals(auth.getName())) {
            throw new InsufficientAuthenticationException("Invalid client authentication");
        }

        //sanity check
        String grantType = parameters.get(OAuth2ParameterNames.GRANT_TYPE);
        if (!type().getValue().equals(grantType)) {
            throw new IllegalArgumentException("invalid grant type");
        }

        log.debug("client token request for {}", auth.getName());

        //generate as admin user
        UserAuthentication<UsernamePasswordAuthenticationToken> user = new UserAuthentication<>(
            auth,
            clientId,
            Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        //full credentials without refresh
        return tokenService.generateToken(user, false, true);
    }

    @Override
    public AuthorizationGrantType type() {
        return AuthorizationGrantType.CLIENT_CREDENTIALS;
    }
}
