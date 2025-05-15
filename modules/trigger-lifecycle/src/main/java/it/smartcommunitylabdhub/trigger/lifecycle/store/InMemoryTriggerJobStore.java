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

package it.smartcommunitylabdhub.trigger.lifecycle.store;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.trigger.lifecycle.models.LifecycleTriggerJob;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;

@Slf4j
public class InMemoryTriggerJobStore implements TriggerJobStore<LifecycleTriggerJob> {

    private final Map<String, LifecycleTriggerJob> store = new ConcurrentHashMap<>();
    private PathMatcher matcher = new AntPathMatcher();

    public void setMatcher(PathMatcher matcher) {
        Assert.notNull(matcher, "matcher is required");
        this.matcher = matcher;
    }

    @Override
    public void store(String id, LifecycleTriggerJob e) {
        Assert.hasText(id, "id must not be empty");
        Assert.notNull(e, "trigger job must not be null");

        log.debug("store trigger job {} in memory", id);
        store.put(id, e);
    }

    @Override
    public void remove(String id) {
        Assert.hasText(id, "id must not be empty");

        if (store.containsKey(id)) {
            log.debug("remove trigger job {} from memory", id);
            store.remove(id);
        }
    }

    @Override
    public LifecycleTriggerJob find(String id) {
        Assert.hasText(id, "id must not be empty");

        log.debug("find trigger job {} in memory", id);
        return store.get(id);
    }

    @Override
    public List<LifecycleTriggerJob> findAll() throws StoreException {
        log.debug("find all trigger jobs in memory");
        return List.copyOf(store.values());
    }

    @Override
    public List<LifecycleTriggerJob> findMatchingKey(String key) throws StoreException {
        Assert.hasText(key, "key must not be empty");

        if (matcher == null) {
            throw new StoreException("matcher is not set");
        }

        return store.values().stream().filter(e -> matcher.match(e.getKey(), key)).toList();
    }

    @Override
    public List<LifecycleTriggerJob> findMatchingKeyAndState(String key, String state) throws StoreException {
        Assert.hasText(key, "key must not be empty");
        Assert.hasText(state, "state must not be empty");

        if (matcher == null) {
            throw new StoreException("matcher is not set");
        }

        return store
            .values()
            .stream()
            .filter(e -> matcher.match(e.getKey(), key) && e.getStates().contains(state))
            .toList();
    }
}
