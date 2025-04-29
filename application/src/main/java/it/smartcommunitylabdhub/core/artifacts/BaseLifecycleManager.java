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

package it.smartcommunitylabdhub.core.artifacts;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import it.smartcommunitylabdhub.commons.models.status.StatusDTO;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.components.run.LifecycleManager;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import it.smartcommunitylabdhub.core.models.events.EntityAction;
import it.smartcommunitylabdhub.core.models.events.EntityOperation;
import it.smartcommunitylabdhub.fsm.Fsm;
import it.smartcommunitylabdhub.fsm.exceptions.InvalidTransitionException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@Slf4j
public abstract class BaseLifecycleManager<
    D extends BaseDTO & StatusDTO,
    E extends BaseEntity,
    T extends Spec,
    S extends Enum<S>,
    X extends Enum<X>,
    C,
    I extends LifecycleEvent<D, S, X>
>
    extends LifecycleManager<D, E> {

    protected abstract Fsm<S, X, C, I> fsm(D dto);

    /*
     * Listen for event callbacks
     */
    @Async
    @EventListener
    public void receive(I event) {
        if (event.getEvent() == null) {
            return;
        }
        try {
            log.debug("receive event {} for {}", event.getEvent(), event.getId());
            if (log.isTraceEnabled()) {
                log.trace("event: {}", event);
            }

            String id = event.getId();

            //load trigger from db
            D dto = entityService.find(event.getId());
            if (dto == null) {
                log.error("Entity with id {} not found", id);
                return;
            }

            //perform lifecycle operation as callback to event
            perform(dto, event.getEvent(), event);
        } catch (StoreException e) {
            log.error("Error with store", e.getMessage());
        }
    }

    /*
     * Perform lifecycle operation
     */
    protected D perform(@NotNull D dto, @NotNull X event) {
        return perform(dto, event, null, null);
    }

    protected D perform(@NotNull D dto, @NotNull X event, @Nullable I input) {
        return perform(dto, event, input, null);
    }

    protected D perform(@NotNull D dto, @NotNull X event, @Nullable I input, @Nullable BiConsumer<D, T> effect) {
        log.debug("{} for {} with id {}", event, dto.getClass().getSimpleName().toLowerCase(), dto.getId());
        if (log.isTraceEnabled()) {
            log.trace("dto: {}", dto);
        }

        // build state machine on current context
        Fsm<S, X, C, I> fsm = fsm(dto);

        //execute update op with locking
        return exec(
            new EntityOperation<>(dto, EntityAction.UPDATE),
            d -> {
                try {
                    //perform via FSM
                    Optional<T> output = fsm.perform(event, input);
                    S state = fsm.getCurrentState();

                    //update status from fsm output
                    Map<String, Serializable> baseStatus = Map.of("state", state);

                    //merge action output into status
                    d.setStatus(
                        MapUtils.mergeMultipleMaps(
                            d.getStatus(),
                            output.isPresent() ? output.get().toMap() : null,
                            baseStatus
                        )
                    );

                    //side effect
                    if (effect != null) {
                        effect.accept(d, output.orElse(null));
                    }

                    return d;
                } catch (InvalidTransitionException e) {
                    log.debug("Invalid transition {} -> {}", e.getFromState(), e.getToState());
                    return d;
                }
            }
        );
    }
}
