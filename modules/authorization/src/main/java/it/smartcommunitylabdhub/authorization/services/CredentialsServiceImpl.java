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

import it.smartcommunitylabdhub.authorization.model.TokenResponse;
import it.smartcommunitylabdhub.authorization.model.TokenResponse.TokenResponseBuilder;
import it.smartcommunitylabdhub.authorization.model.UserAuthentication;
import it.smartcommunitylabdhub.authorization.providers.AccessCredentials;
import it.smartcommunitylabdhub.authorization.providers.AccessCredentialsProvider;
import it.smartcommunitylabdhub.commons.infrastructure.Credentials;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CredentialsServiceImpl implements CredentialsService, TokenService {

    private final List<CredentialsProvider> providers;
    private AccessCredentialsProvider accessProvider;

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
    public void setAccessProvider(AccessCredentialsProvider credentialsProvider) {
        this.accessProvider = credentialsProvider;
    }

    public List<Credentials> getCredentials(@NotNull UserAuthentication<?> auth) {
        log.debug("get credentials from providers for user {}", auth.getName());
        List<Credentials> credentials = providers.stream().map(p -> p.get(auth)).filter(c -> c != null).toList();

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
        boolean withCredentials,
        boolean withRefresh
    ) {
        log.info("generate credentials for user {}", authentication.getName());

        List<CredentialsProvider> credentialsProviders = providers
            .stream()
            .filter(p -> !(p instanceof AccessCredentialsProvider))
            .toList();

        if (withCredentials) {
            //refresh credentials before token generation
            List<Credentials> credentials = credentialsProviders
                .stream()
                .map(p -> p.process(authentication))
                .filter(c -> c != null)
                .collect(Collectors.toList());
            authentication.setCredentials(credentials);
        }

        //response
        TokenResponseBuilder response = TokenResponse.builder();

        if (accessProvider != null) {
            AccessCredentials c = accessProvider.get(authentication);
            if (c != null) {
                //copy credential as token response
                response.accessToken(c.getAccessTokenAsString());
                response.idToken(c.getIdTokenAsString());
                response.clientId(c.getClientId());
                response.expiration(c.getExpiration());
                response.issuer(c.getIssuer());

                if (withRefresh) {
                    response.refreshToken(c.getRefreshToken());
                }
            }
        }

        if (withCredentials) {
            //derive full credentials as map
            response.credentials(
                credentialsProviders
                    .stream()
                    .map(p -> p.get(authentication))
                    .filter(c -> c != null)
                    .flatMap(c -> c.toMap().entrySet().stream())
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()))
            );
        }

        return response.build();
    }

    public TokenResponse generateToken(@NotNull UserAuthentication<?> authentication) {
        return generateToken(authentication, true, true);
    }
}
