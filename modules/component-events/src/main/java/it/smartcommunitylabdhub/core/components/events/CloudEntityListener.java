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

package it.smartcommunitylabdhub.core.components.events;

@Component
@Slf4j
public class    CloudEntityListener implements EntityListener<T extends BaseEntity> {
    @Override
    public void onCreate(T entity) {
        log.debug("create entity {}", entity.getId());
    }

    @Override
    public void onUpdate(T entity) {
        log.debug("update entity {}", entity.getId());
    }

    @Override
    public void onDelete(T entity) {
        log.debug("delete entity {}", entity.getId());
    }


    protected void broadcast(EntityEvent<E> event) {
        log.debug("broadcast event for {} {}", clazz.getSimpleName(), event.getAction());
        //publish external event
        if (eventPublisher != null) {
            E entity = event.getEntity();
            if (log.isTraceEnabled()) {
                log.trace("{}: {}", clazz.getSimpleName(), String.valueOf(entity));
            }

            if (entity == null) {
                return;
            }

            T dto = converter.convert(entity);

            log.debug("publish cloud event: {} for {} {}", event.getAction(), clazz.getSimpleName(), dto.getId());
            CloudEntityEvent<T> cloud = new CloudEntityEvent<>(dto, clazz, event.getAction());
            if (log.isTraceEnabled()) {
                log.trace("cloud event: {}", String.valueOf(cloud));
            }

            eventPublisher.publishEvent(cloud);
        }
    }

    protected void notify(String user, EntityEvent<E> event) {
        log.debug("notify event for {} {}", clazz.getSimpleName(), event.getAction());
        //publish external event
        if (eventPublisher != null) {
            E entity = event.getEntity();
            if (log.isTraceEnabled()) {
                log.trace("{}: {}", clazz.getSimpleName(), String.valueOf(entity));
            }

            if (entity == null) {
                return;
            }

            T dto = converter.convert(entity);

            log.debug("publish notify event: {} for {} {}", event.getAction(), clazz.getSimpleName(), dto.getId());
            UserNotificationEntityEvent<T> cloud = new UserNotificationEntityEvent<>(
                user,
                dto,
                clazz,
                event.getAction()
            );
            if (log.isTraceEnabled()) {
                log.trace("notify event: {}", String.valueOf(cloud));
            }

            eventPublisher.publishEvent(cloud);
        }
    }
}