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

package it.smartcommunitylabdhub.files.provider;

import it.smartcommunitylabdhub.authorization.model.UserAuthentication;
import it.smartcommunitylabdhub.authorization.services.CredentialsProvider;
import it.smartcommunitylabdhub.commons.infrastructure.ConfigurationProvider;
import it.smartcommunitylabdhub.commons.infrastructure.Credentials;
import it.smartcommunitylabdhub.files.provider.S3Config.S3ConfigBuilder;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class S3Provider implements ConfigurationProvider, CredentialsProvider, InitializingBean {

    @Value("${files.store.s3.access-key}")
    private String accessKey;

    @Value("${files.store.s3.secret-key}")
    private String secretKey;

    @Value("${files.store.s3.endpoint}")
    private String endpoint;

    @Value("${files.store.s3.bucket}")
    private String bucket;

    @Value("${files.store.s3.signature-version}")
    private String signatureVersion;

    @Value("${files.store.s3.region}")
    private String region;

    @Value("${credentials.provider.s3.enable}")
    private Boolean enabled;

    private S3Config config;

    public S3Provider() {
        //TODO define a property bean
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (endpoint != null) {
            log.debug("Build configuration for provider...");

            //build config
            S3ConfigBuilder builder = S3Config
                .builder()
                .endpoint(endpoint)
                .bucket(bucket)
                .region(region)
                .signatureVersion(signatureVersion);

            this.config = builder.build();

            if (log.isTraceEnabled()) {
                log.trace("config: {}", config.toJson());
            }
        }
    }

    @Override
    public Credentials get(@NotNull UserAuthentication<?> auth) {
        if (config == null) {
            return null;
        }

        if (Boolean.TRUE.equals(enabled)) {
            log.debug("generate credentials for user authentication {} via STS service", auth.getName());

            //static credentials shared
            return S3Credentials.builder().accessKey(accessKey).secretKey(secretKey).build();
        }

        return null;
    }

    @Override
    public S3Config getConfig() {
        return config;
    }

    @Override
    public <T extends AbstractAuthenticationToken> Credentials process(@NotNull T token) {
        //nothing to do, this provider is static
        return null;
    }
}
