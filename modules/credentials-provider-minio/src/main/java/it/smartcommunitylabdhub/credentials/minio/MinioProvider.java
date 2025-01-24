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

package it.smartcommunitylabdhub.credentials.minio;

import io.minio.credentials.AssumeRoleProvider;
import it.smartcommunitylabdhub.authorization.model.UserAuthentication;
import it.smartcommunitylabdhub.authorization.services.CredentialsProvider;
import it.smartcommunitylabdhub.commons.infrastructure.Credentials;
import jakarta.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class MinioProvider implements CredentialsProvider, InitializingBean {

    @Value("${credentials.provider.minio.access-key}")
    private String accessKey;

    @Value("${credentials.provider.minio.secret-key}")
    private String secretKey;

    @Value("${credentials.provider.minio.claim}")
    private String claim;

    @Value("${credentials.provider.minio.policy}")
    private String defaultPolicy;

    @Value("${credentials.provider.minio.duration}")
    private Integer defaultDuration;

    @Value("${credentials.provider.minio.endpoint}")
    private String endpointUrl;

    @Value("${credentials.provider.minio.enable}")
    private Boolean enabled;

    public MinioProvider() {
        //TODO define a property bean
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Boolean.TRUE.equals(enabled)) {
            //check config
            enabled =
                StringUtils.hasText(accessKey) && StringUtils.hasText(secretKey) && StringUtils.hasText(endpointUrl);
        }
    }

    @Override
    public Credentials get(@NotNull UserAuthentication<?> auth) {
        if (Boolean.TRUE.equals(enabled)) {
            //we expect a policy credentials in context
            MinioPolicy policy = Optional
                .ofNullable(auth.getCredentials())
                .map(creds ->
                    creds
                        .stream()
                        .filter(MinioPolicy.class::isInstance)
                        .map(c -> (MinioPolicy) c)
                        .findFirst()
                        .orElse(null)
                )
                .orElse(null);

            if (policy != null) {
                //TODO cache on username+policy for EXPIRE-skew
                log.debug("generate credentials for user authentication {} via STS service", auth.getName());

                try {
                    AssumeRoleProvider provider = new AssumeRoleProvider(
                        endpointUrl,
                        accessKey,
                        secretKey,
                        defaultDuration,
                        policy.getPolicy(),
                        null,
                        null,
                        null,
                        null,
                        null
                    );

                    io.minio.credentials.Credentials response = provider.fetch();

                    return MinioSessionCredentials
                        .builder()
                        .accessKey(response.accessKey())
                        .secretKey(response.secretKey())
                        .sessionToken(response.sessionToken())
                        .build();
                } catch (NoSuchAlgorithmException | ProviderException e) {
                    //error, no recovery
                    log.error("Error with provider {}", e);
                }
            }
        }

        return null;
    }

    @Override
    public <T extends AbstractAuthenticationToken> Credentials process(@NotNull T token) {
        //extract a policy from jwt tokens
        if (token instanceof JwtAuthenticationToken && StringUtils.hasText(claim)) {
            String policy = ((JwtAuthenticationToken) token).getToken().getClaimAsString(claim);
            if (StringUtils.hasText(policy)) {
                return MinioPolicy.builder().claim(claim).policy(policy).build();
            }
        }

        //fallback to default
        if (StringUtils.hasText(defaultPolicy)) {
            return MinioPolicy.builder().claim(claim).policy(defaultPolicy).build();
        }

        return null;
    }
}
