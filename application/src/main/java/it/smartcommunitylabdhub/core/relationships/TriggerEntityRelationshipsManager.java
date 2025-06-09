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

package it.smartcommunitylabdhub.core.relationships;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.metadata.RelationshipsMetadata;
import it.smartcommunitylabdhub.commons.models.relationships.RelationshipDetail;
import it.smartcommunitylabdhub.commons.models.trigger.Trigger;
import it.smartcommunitylabdhub.core.relationships.persistence.RelationshipEntity;
import it.smartcommunitylabdhub.core.triggers.persistence.TriggerDTOBuilder;
import it.smartcommunitylabdhub.core.triggers.persistence.TriggerEntity;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class TriggerEntityRelationshipsManager extends BaseEntityRelationshipsManager<TriggerEntity> {

    private static final EntityName TYPE = EntityName.TRIGGER;

    private final TriggerDTOBuilder builder;

    public TriggerEntityRelationshipsManager(TriggerDTOBuilder builder) {
        Assert.notNull(builder, "builder can not be null");
        this.builder = builder;
    }

    @Override
    public void register(TriggerEntity entity) throws StoreException {
        Assert.notNull(entity, "entity can not be null");

        Trigger item = builder.convert(entity);
        if (item == null) {
            throw new IllegalArgumentException("invalid or null entity");
        }

        log.debug("register for trigger {}", entity.getId());

        RelationshipsMetadata relationships = RelationshipsMetadata.from(item.getMetadata());
        service.register(item.getProject(), TYPE, item.getId(), item.getKey(), relationships.getRelationships());
    }

    @Override
    public void clear(TriggerEntity entity) throws StoreException {
        Assert.notNull(entity, "entity can not be null");

        Trigger item = builder.convert(entity);
        if (item == null) {
            throw new IllegalArgumentException("invalid or null entity");
        }

        log.debug("clear for trigger {}", entity.getId());

        service.clear(item.getProject(), TYPE, item.getId());
    }

    @Override
    public List<RelationshipDetail> getRelationships(TriggerEntity entity) throws StoreException {
        Assert.notNull(entity, "entity can not be null");

        log.debug("get for trigger {}", entity.getId());

        List<RelationshipEntity> entries = service.listByEntity(entity.getProject(), TYPE, entity.getId());
        return entries
            .stream()
            .map(e -> new RelationshipDetail(e.getRelationship(), e.getSourceKey(), e.getDestKey()))
            .toList();
    }
}
