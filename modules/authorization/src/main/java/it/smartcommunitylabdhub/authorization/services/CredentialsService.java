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

import it.smartcommunitylabdhub.authorization.model.UserAuthentication;
import it.smartcommunitylabdhub.commons.infrastructure.Credentials;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CredentialsService {

    private final List<CredentialsProvider> providers;

    public CredentialsService(Collection<CredentialsProvider> providers) {
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

    public List<Credentials> getCredentials(@NotNull UserAuthentication<?> auth) {
        return getCredentials(auth, false);
    }

    public List<Credentials> getCredentials(@NotNull UserAuthentication<?> auth, boolean refresh) {
        if (refresh || auth.getCredentials() == null || auth.getCredentials().isEmpty()) {
            log.debug("get credentials from providers for user {}", auth.getName());
            List<Credentials> credentials = providers.stream().map(p -> p.get(auth)).toList();

            //cache
            auth.setCredentials(credentials);

            return credentials;
        }

        //use cached values
        //note: we don't evaluate duration because core is stateless, so max duration of cache is call duration
        log.debug("use cached credentials for user {}", auth.getName());

        return auth.getCredentials();
    }
}
