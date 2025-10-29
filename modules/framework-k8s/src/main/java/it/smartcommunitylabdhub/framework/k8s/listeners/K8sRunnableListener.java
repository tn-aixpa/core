/*
 * SPDX-FileCopyrightText: © 2025 DSLab - Fondazione Bruno Kessler
 *
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package it.smartcommunitylabdhub.framework.k8s.listeners;

import it.smartcommunitylabdhub.commons.exceptions.FrameworkException;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sBaseFramework;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnableState;
import it.smartcommunitylabdhub.runtimes.events.RunnableChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;

@Slf4j
public abstract class K8sRunnableListener<R extends K8sRunnable> {

    private final Class<R> clazz;

    private final K8sBaseFramework<R, ?> k8sFramework;

    private final RunnableStore<R> runnableStore;

    private ApplicationEventPublisher eventPublisher;

    @SuppressWarnings("unchecked")
    protected K8sRunnableListener(K8sBaseFramework<R, ?> k8sFramework, RunnableStore<R> runnableStore) {
        Assert.notNull(k8sFramework, "k8sFramework can not be null");
        Assert.notNull(runnableStore, "runnableStore can not be null");

        this.k8sFramework = k8sFramework;
        this.runnableStore = runnableStore;

        this.clazz = (Class<R>) runnableStore.getResolvableType().resolve();
        log.debug("started listener for {} with framework {}", clazz.getName(), k8sFramework.getClass().getName());
    }

    public void process(R runnable) {
        Assert.notNull(runnable, "runnable can not be null");
        Assert.hasText(runnable.getId(), "runnable id can not be null or empty");
        log.info(
            "Receive runnable {} for execution: {} state {}",
            clazz.getSimpleName(),
            runnable.getId(),
            runnable.getState()
        );

        if (log.isTraceEnabled()) {
            log.trace("runnable {}: {}", clazz.getSimpleName(), runnable);
        }

        String id = runnable.getId();
        String framework = runnable.getFramework();
        String state = runnable.getState();

        try {
            runnable =
                switch (K8sRunnableState.valueOf(state)) {
                    case K8sRunnableState.READY -> {
                        //sanity check: reset left-over messages
                        runnable.setMessage(null);
                        yield k8sFramework.run(runnable);
                    }
                    case K8sRunnableState.STOP -> {
                        //sanity check: reset left-over messages
                        runnable.setMessage(null);
                        yield k8sFramework.stop(runnable);
                    }
                    case K8sRunnableState.RESUME -> {
                        //sanity check: reset left-over messages
                        runnable.setMessage(null);
                        yield k8sFramework.resume(runnable);
                    }
                    case K8sRunnableState.DELETING -> {
                        //sanity check: reset left-over messages
                        runnable.setMessage(null);
                        yield k8sFramework.delete(runnable);
                    }
                    default -> {
                        yield null;
                    }
                };

            if (runnable != null) {
                //sanity check: id+framework can not change
                if (!id.equals(runnable.getId()) || !framework.equals(runnable.getFramework())) {
                    throw new IllegalArgumentException("id mismatch");
                }

                if (log.isTraceEnabled()) {
                    log.trace("runnable result from framework {}: {}", clazz.getSimpleName(), runnable);
                }
            }
        } catch (FrameworkException e) {
            // Set runnable to error state send event
            log.error("Error with k8s for runnable {} {}: {}", clazz.getSimpleName(), id, e.getMessage());
            if (runnable != null) {
                runnable.setState(K8sRunnableState.ERROR.name());
                runnable.setError(clazz.getSimpleName() + ":" + String.valueOf(e.getMessage()));

                if (e instanceof K8sFrameworkException) {
                    runnable.setError(((K8sFrameworkException) e).toError());
                }
            }
        } catch (RuntimeException e) {
            // Set runnable to error state send event
            log.error("Error for runnable {} {}: {}", clazz.getSimpleName(), id, e.getMessage());
            if (runnable != null) {
                runnable.setState(K8sRunnableState.ERROR.name());
                runnable.setError(String.valueOf(e.getMessage()));
            }
        } finally {
            if (runnable != null) {
                try {
                    log.debug("update runnable {} {} in store", clazz.getSimpleName(), id);
                    runnableStore.store(id, runnable);
                } catch (StoreException se) {
                    log.error("Error with store: {}", se.getMessage());
                }

                log.debug("Processed runnable {} {}", clazz.getSimpleName(), id);

                if (eventPublisher != null) {
                    log.debug("Publish runnable {} {}", clazz.getSimpleName(), id);

                    RunnableChangedEvent<RunRunnable> event = RunnableChangedEvent.build(runnable, state);

                    if (log.isTraceEnabled()) {
                        log.trace("runnable {} {} event {}", clazz.getSimpleName(), id, event);
                    }

                    // Publish event to Run Manager
                    eventPublisher.publishEvent(event);
                }
            }
        }
    }

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
