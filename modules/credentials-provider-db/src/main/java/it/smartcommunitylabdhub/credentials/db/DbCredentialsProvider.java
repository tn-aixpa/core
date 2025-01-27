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

package it.smartcommunitylabdhub.credentials.db;

import it.smartcommunitylabdhub.authorization.model.UserAuthentication;
import it.smartcommunitylabdhub.authorization.services.CredentialsProvider;
import it.smartcommunitylabdhub.commons.infrastructure.Credentials;
import jakarta.validation.constraints.NotNull;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class DbCredentialsProvider implements CredentialsProvider, InitializingBean {

    @Value("${credentials.provider.db.user}")
    private String user;

    @Value("${credentials.provider.db.password}")
    private String secret;

    @Value("${credentials.provider.db.database}")
    private String database;

    @Value("${credentials.provider.db.claim}")
    private String claim;

    @Value("${credentials.provider.db.role}")
    private String defaultRole;

    @Value("${credentials.provider.db.duration}")
    private Integer defaultDuration;

    @Value("${credentials.provider.db.endpoint}")
    private String endpointUrl;

    @Value("${credentials.provider.db.enable}")
    private Boolean enabled;

    private final RestTemplate restTemplate;

    public DbCredentialsProvider() {
        //TODO define a property bean
        restTemplate = new RestTemplate();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Boolean.TRUE.equals(enabled)) {
            //check config
            enabled = StringUtils.hasText(user) && StringUtils.hasText(secret) && StringUtils.hasText(endpointUrl);
        }
    }

    @Override
    public Credentials get(@NotNull UserAuthentication<?> auth) {
        if (Boolean.TRUE.equals(enabled)) {
            //we expect a role credentials in context
            DbRole role = Optional
                .ofNullable(auth.getCredentials())
                .map(creds ->
                    creds.stream().filter(DbRole.class::isInstance).map(c -> (DbRole) c).findFirst().orElse(null)
                )
                .orElse(null);

            if (role != null) {
                //TODO cache on username+role for EXPIRE-skew
                String username = auth.getName();
                log.debug("generate credentials for user authentication {} via STS service", username);

                try {
                    HttpHeaders headers = new HttpHeaders();
                    if (StringUtils.hasText(user) && StringUtils.hasText(secret)) {
                        //basic auth is required
                        byte[] basicAuth = Base64
                            .getEncoder()
                            .encode((user + ":" + secret).getBytes(Charset.forName("US-ASCII")));
                        headers.add("Authorization", "Basic " + new String(basicAuth));
                    }

                    TokenRequest request = TokenRequest
                        .builder()
                        .username(username)
                        .database(database)
                        .roles(Collections.singleton(role.getRole()))
                        .duration(defaultDuration)
                        .build();

                    if (log.isTraceEnabled()) {
                        log.trace("request: {}", request);
                    }

                    ResponseEntity<TokenResponse> response = restTemplate.exchange(
                        endpointUrl,
                        HttpMethod.POST,
                        new HttpEntity<>(request, headers),
                        TokenResponse.class
                    );

                    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                        //error, no recovery
                        log.error("Error with provider, status code {}", response.getStatusCode().toString());
                        return null;
                    }

                    TokenResponse token = response.getBody();
                    if (log.isTraceEnabled()) {
                        log.trace("response: {}", token);
                    }

                    return DbCredentials
                        .builder()
                        .platform(token.getPlatform())
                        .host(token.getHost())
                        .port(token.getPort())
                        .username(token.getUsername())
                        .password(token.getPassword())
                        .database(token.getDatabase())
                        .build();
                } catch (RestClientException e) {
                    //error, no recovery
                    log.error("Error with provider {}", e);
                }
            }
        }

        return null;
    }

    @Override
    public <T extends AbstractAuthenticationToken> Credentials process(@NotNull T token) {
        //extract a role from jwt tokens
        if (token instanceof JwtAuthenticationToken && StringUtils.hasText(claim)) {
            String role = ((JwtAuthenticationToken) token).getToken().getClaimAsString(claim);
            if (StringUtils.hasText(role)) {
                return DbRole.builder().claim(claim).role(role).build();
            }
        }

        //fallback to default
        if (StringUtils.hasText(defaultRole)) {
            return DbRole.builder().claim(claim).role(defaultRole).build();
        }

        return null;
    }
}
