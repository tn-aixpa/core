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

package it.smartcommunitylabdhub.authorization.services;

import com.nimbusds.jwt.SignedJWT;
import it.smartcommunitylabdhub.authorization.model.TokenResponse;
import it.smartcommunitylabdhub.authorization.model.TokenResponse.TokenResponseBuilder;
import it.smartcommunitylabdhub.authorization.model.UserAuthentication;
import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import it.smartcommunitylabdhub.commons.infrastructure.Credentials;
import jakarta.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
public class CredentialsServiceImpl implements CredentialsService, TokenService, InitializingBean {

    @Autowired
    private ApplicationProperties applicationProperties;

    private final List<CredentialsProvider> providers;
    private JwtTokenService jwtTokenService;

    public CredentialsServiceImpl(Collection<CredentialsProvider> providers) {
        log.debug("Initialize service with providers");

        if (providers != null) {
            if (log.isTraceEnabled()) {
                log.trace("providers: {}", providers);
            }

            this.providers = Collections.unmodifiableList(new ArrayList<>(providers));
        } else {
            this.providers = Collections.emptyList();
        }
    }

    @Autowired
    public void setJwtTokenService(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(jwtTokenService, "jwt token service is required");
    }

    public List<Credentials> getCredentials(@NotNull UserAuthentication<?> auth) {
        log.debug("get credentials from providers for user {}", auth.getName());
        List<Credentials> credentials = providers.stream().map(p -> p.get(auth)).toList();

        if (log.isTraceEnabled()) {
            log.trace("credentials: {}", credentials);
        }

        return credentials;
    }

    //DISABLED: do not store credentials in user auth because we may serialize it
    // public List<Credentials> getCredentials(@NotNull UserAuthentication<?> auth, boolean refresh) {
    //     if (refresh || auth.getCredentials() == null || auth.getCredentials().isEmpty()) {
    //         log.debug("get credentials from providers for user {}", auth.getName());
    //         List<Credentials> credentials = providers.stream().map(p -> p.get(auth)).toList();

    //         //cache
    //         auth.setCredentials(credentials);

    //         return credentials;
    //     }

    //     //use cached values
    //     //note: we don't evaluate duration because core is stateless, so max duration of cache is call duration
    //     log.debug("use cached credentials for user {}", auth.getName());

    //     return auth.getCredentials();
    // }

    public TokenResponse generateToken(
        @NotNull UserAuthentication<?> authentication,
        boolean withRefresh,
        boolean withCredentials
    ) {
        if (withCredentials) {
            //refresh credentials before token generation
            List<Credentials> credentials = providers
                .stream()
                .map(p -> p.process(authentication))
                .collect(Collectors.toList());
            authentication.setCredentials(credentials);
        }

        // Serialize to compact form
        SignedJWT accessToken = jwtTokenService.generateAccessToken(authentication);
        String refreshToken = withRefresh ? jwtTokenService.generateRefreshToken(authentication, accessToken) : null;

        Integer exp = null;
        try {
            if (
                accessToken != null &&
                accessToken.getJWTClaimsSet().getExpirationTime() != null &&
                accessToken.getJWTClaimsSet().getIssueTime() != null
            ) {
                exp =
                    (int) ((accessToken.getJWTClaimsSet().getExpirationTime().getTime() -
                            accessToken.getJWTClaimsSet().getIssueTime().getTime()) /
                        1000);
            }
        } catch (ParseException | NullPointerException e) {
            //invalid duration, ignore
        }

        //response
        TokenResponseBuilder response = TokenResponse
            .builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiration(exp)
            .clientId(jwtTokenService.getClientId())
            .issuer(applicationProperties.getEndpoint());

        if (withCredentials) {
            //derive full credentials as map
            response.credentials(
                providers
                    .stream()
                    .flatMap(p -> p.get(authentication).toMap().entrySet().stream())
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()))
            );
        }

        return response.build();
    }

    public TokenResponse generateToken(@NotNull UserAuthentication<?> authentication) {
        return generateToken(authentication, false, true);
    }
}
