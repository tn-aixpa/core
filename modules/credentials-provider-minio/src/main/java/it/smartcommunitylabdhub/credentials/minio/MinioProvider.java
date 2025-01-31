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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.minio.credentials.AssumeRoleProvider;
import it.smartcommunitylabdhub.authorization.model.UserAuthentication;
import it.smartcommunitylabdhub.authorization.services.CredentialsProvider;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.infrastructure.Credentials;
import jakarta.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class MinioProvider implements CredentialsProvider, InitializingBean {

    private static final Integer DEFAULT_DURATION = 24 * 3600; //24 hour
    private static final Integer MIN_DURATION = 300; //5 min

    @Value("${credentials.provider.minio.access-key}")
    private String accessKey;

    @Value("${credentials.provider.minio.secret-key}")
    private String secretKey;

    @Value("${credentials.provider.minio.claim}")
    private String claim;

    @Value("${credentials.provider.minio.policy}")
    private String defaultPolicy;

    @Value("${credentials.provider.minio.duration}")
    private Integer duration = DEFAULT_DURATION;

    @Value("${credentials.provider.minio.endpoint}")
    private String endpointUrl;

    @Value("${credentials.provider.minio.region}")
    private String region;

    @Value("${credentials.provider.minio.enable}")
    private Boolean enabled;

    // cache credentials for up to DURATION
    LoadingCache<Pair<String, String>, MinioSessionCredentials> cache;

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

        if (duration == null || duration < MIN_DURATION) {
            duration = DEFAULT_DURATION;
        }

        //keep cache shorter than token duration to avoid releasing soon to be expired keys
        int cacheDuration = Math.max((duration - MIN_DURATION), MIN_DURATION);

        //initialize cache
        cache =
            CacheBuilder
                .newBuilder()
                .expireAfterWrite(cacheDuration, TimeUnit.SECONDS)
                .build(
                    new CacheLoader<Pair<String, String>, MinioSessionCredentials>() {
                        @Override
                        public MinioSessionCredentials load(@Nonnull Pair<String, String> key) throws Exception {
                            log.debug("load credentials for {} policy {}", key.getFirst(), key.getSecond());
                            return generate(key.getFirst(), key.getSecond());
                        }
                    }
                );
    }

    private MinioSessionCredentials generate(@NotNull String username, @NotNull String policy) throws StoreException {
        log.debug("generate credentials for user authentication {} policy {} via STS service", username, policy);

        try {
            //assume role as user
            //NOTE: roleArn or policy scoping does NOT work via assumeRole, only with external OIDC provider
            //credentials will receive the same set of privileges as the accessKey used to sign the request!
            AssumeRoleProvider provider = new AssumeRoleProvider(
                endpointUrl,
                accessKey,
                secretKey,
                duration,
                null,
                region,
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
                .endpoint(endpointUrl)
                .region(region)
                .signatureVersion("s3v4")
                .build();
        } catch (NoSuchAlgorithmException | ProviderException e) {
            //error, no recovery
            log.error("Error with provider {}", e);
            throw new StoreException(e.getMessage());
        }
    }

    @Override
    public Credentials get(@NotNull UserAuthentication<?> auth) {
        if (Boolean.TRUE.equals(enabled) && cache != null) {
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

            if (policy != null && StringUtils.hasText(policy.getPolicy())) {
                String username = auth.getName();
                log.debug("get credentials for user authentication {} from cache", username);
                try {
                    return cache.get(Pair.of(username, policy.getPolicy()));
                } catch (ExecutionException e) {
                    //error, no recovery
                    log.error("Error with provider {}", e);
                }
                // //TODO cache on username+policy for EXPIRE-skew
                // log.debug("generate credentials for user authentication {} via STS service", auth.getName());

                // try {
                //     AssumeRoleProvider provider = new AssumeRoleProvider(
                //         endpointUrl,
                //         accessKey,
                //         secretKey,
                //         defaultDuration,
                //         policy.getPolicy(),
                //         region,
                //         null,
                //         null,
                //         null,
                //         null
                //     );

                //     io.minio.credentials.Credentials response = provider.fetch();

                //     return MinioSessionCredentials
                //         .builder()
                //         .accessKey(response.accessKey())
                //         .secretKey(response.secretKey())
                //         .sessionToken(response.sessionToken())
                //         .endpoint(endpointUrl)
                //         .region(region)
                //         .signatureVersion("s3v4")
                //         .build();
                // } catch (NoSuchAlgorithmException | ProviderException e) {
                //     //error, no recovery
                //     log.error("Error with provider {}", e);
                // }
            }
        }

        return null;
    }

    @Override
    public <T extends AbstractAuthenticationToken> Credentials process(@NotNull T token) {
        if (Boolean.TRUE.equals(enabled)) {
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
        }

        return null;
    }
}
