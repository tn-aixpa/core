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

package it.smartcommunitylabdhub.authorization.repositories;

import it.smartcommunitylabdhub.authorization.model.AuthorizationRequest;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.util.Assert;

public class AuthorizationRequestStore implements Serializable {

    private final Map<String, AuthorizationRequest> requests;

    public AuthorizationRequestStore() {
        this.requests = new ConcurrentHashMap<>();
    }

    public AuthorizationRequest find(String key) {
        Assert.hasText(key, "key can not be null or empty");
        return requests.get(key);
    }

    public Collection<AuthorizationRequest> findAll() {
        return Collections.unmodifiableCollection(requests.values());
    }

    public String store(AuthorizationRequest request) {
        String key = extractKey(request);
        requests.put(key, request);

        return key;
    }

    public AuthorizationRequest consume(String key) {
        Assert.hasText(key, "key can not be null or empty");
        return requests.remove(key);
    }

    public void remove(String key) {
        requests.remove(key);
    }

    private String extractKey(AuthorizationRequest request) {
        //we use state+clientId as key because we receive those from token request as well
        return request.getState() + request.getClientId();
    }
}
