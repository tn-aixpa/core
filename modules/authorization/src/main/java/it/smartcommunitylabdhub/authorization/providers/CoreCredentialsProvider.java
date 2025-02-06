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

package it.smartcommunitylabdhub.authorization.providers;

import com.nimbusds.jwt.SignedJWT;
import it.smartcommunitylabdhub.authorization.model.UserAuthentication;
import it.smartcommunitylabdhub.authorization.providers.CoreCredentials.CoreCredentialsBuilder;
import it.smartcommunitylabdhub.authorization.providers.CoreCredentialsConfig.CoreCredentialsConfigBuilder;
import it.smartcommunitylabdhub.authorization.services.AuthorizableAwareEntityService;
import it.smartcommunitylabdhub.authorization.services.CredentialsProvider;
import it.smartcommunitylabdhub.authorization.services.JwtTokenService;
import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import it.smartcommunitylabdhub.commons.config.SecurityProperties;
import it.smartcommunitylabdhub.commons.infrastructure.ConfigurationProvider;
import it.smartcommunitylabdhub.commons.infrastructure.Credentials;
import it.smartcommunitylabdhub.commons.models.project.Project;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
public class CoreCredentialsProvider implements ConfigurationProvider, CredentialsProvider, InitializingBean {

    private JwtTokenService jwtTokenService;
    private CoreCredentialsConfig config;

    AuthorizableAwareEntityService<Project> projectAuthHelper;

    public CoreCredentialsProvider(
        JwtTokenService jwtTokenService,
        ApplicationProperties properties,
        SecurityProperties security
    ) {
        Assert.notNull(jwtTokenService, "token service is required");
        Assert.notNull(properties, "properties can not be null");
        Assert.notNull(security, "properties can not be null");

        log.debug("Build configuration for provider...");
        if (security.isRequired()) {
            this.jwtTokenService = jwtTokenService;

            String baseUrl = properties.getEndpoint();

            //build config
            CoreCredentialsConfigBuilder builder = CoreCredentialsConfig.builder();
            Set<String> authMethods = new HashSet<>();

            //basic
            if (security.isBasicAuthEnabled()) {
                authMethods.add("basic");
                builder.realm(baseUrl);
            }

            //oauth2
            if (security.isJwtAuthEnabled()) {
                authMethods.add("jwt");
            }

            if (security.isOidcAuthEnabled()) {
                authMethods.add("oidc");
            }

            builder.authenticationMethods(authMethods);

            this.config = builder.build();

            if (log.isTraceEnabled()) {
                log.trace("config: {}", config.toJson());
            }
        }
    }

    @Autowired(required = false)
    public void setProjectAuthHelper(AuthorizableAwareEntityService<Project> projectAuthHelper) {
        this.projectAuthHelper = projectAuthHelper;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(config, "config not initialized");
    }

    @Override
    public CoreCredentials get(@NotNull UserAuthentication<?> auth) {
        if (jwtTokenService == null) {
            return null;
        }

        log.debug("generate credentials for user authentication {} via jwtToken service", auth.getName());
        //TODO evaluate caching responses
        //NOTE: refresh token is bound to access and single use, we could cache only non-refreshable cred!
        SignedJWT accessToken = jwtTokenService.generateAccessToken(auth);
        String refreshToken = jwtTokenService.generateRefreshToken(auth, accessToken);

        Integer exp = jwtTokenService.getAccessTokenDuration();
        //response
        CoreCredentialsBuilder response = CoreCredentials
            .builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .idToken(accessToken)
            .expiration(exp)
            .clientId(jwtTokenService.getClientId())
            .issuer(jwtTokenService.getIssuer());

        return response.build();
    }

    @Override
    public CoreCredentialsConfig getConfig() {
        return config;
    }

    @Override
    public <T extends AbstractAuthenticationToken> Credentials process(@NotNull T token) {
        //inflate token by adding project authorizations
        String username = token.getName();

        if (projectAuthHelper == null) {
            return null;
        }

        Set<String> projects = new HashSet<>();

        //inject roles from ownership of projects
        //derive a scoped ADMIN role
        projectAuthHelper
            .findIdsByCreatedBy(username)
            .forEach(p -> {
                projects.add(p);
            });

        //inject roles from sharing of projects
        //derive a scoped USER role
        //TODO make configurable?
        projectAuthHelper
            .findIdsBySharedTo(username)
            .forEach(p -> {
                projects.add(p);
            });

        CoreCredentials cred = CoreCredentials.builder().projects(projects).build();

        return cred;
    }
}
