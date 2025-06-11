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

package it.smartcommunitylabdhub.core.artifacts.relationships;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.metadata.RelationshipsMetadata;
import it.smartcommunitylabdhub.commons.models.relationships.RelationshipDetail;
import it.smartcommunitylabdhub.core.artifacts.builders.ArtifactDTOBuilder;
import it.smartcommunitylabdhub.core.artifacts.persistence.ArtifactEntity;
import it.smartcommunitylabdhub.core.relationships.BaseEntityRelationshipsManager;
import it.smartcommunitylabdhub.core.relationships.persistence.RelationshipEntity;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class ArtifactEntityRelationshipsManager extends BaseEntityRelationshipsManager<ArtifactEntity> {

    private static final EntityName TYPE = EntityName.ARTIFACT;

    private final ArtifactDTOBuilder builder;

    public ArtifactEntityRelationshipsManager(ArtifactDTOBuilder builder) {
        Assert.notNull(builder, "builder can not be null");
        this.builder = builder;
    }

    @Override
    public void register(ArtifactEntity entity) throws StoreException {
        Assert.notNull(entity, "entity can not be null");

        Artifact item = builder.convert(entity);
        if (item == null) {
            throw new IllegalArgumentException("invalid or null entity");
        }

        log.debug("register for artifact {}", entity.getId());

        RelationshipsMetadata relationships = RelationshipsMetadata.from(item.getMetadata());
        service.register(item.getProject(), TYPE, item.getId(), item.getKey(), relationships.getRelationships());
    }

    @Override
    public void clear(ArtifactEntity entity) throws StoreException {
        Assert.notNull(entity, "entity can not be null");

        Artifact item = builder.convert(entity);
        if (item == null) {
            throw new IllegalArgumentException("invalid or null entity");
        }

        log.debug("clear for artifact {}", entity.getId());

        service.clear(item.getProject(), TYPE, item.getId());
    }

    @Override
    public List<RelationshipDetail> getRelationships(ArtifactEntity entity) throws StoreException {
        Assert.notNull(entity, "entity can not be null");

        log.debug("get for artifact {}", entity.getId());

        List<RelationshipEntity> entries = service.listByEntity(entity.getProject(), TYPE, entity.getId());
        return entries
            .stream()
            .map(e -> new RelationshipDetail(e.getRelationship(), e.getSourceKey(), e.getDestKey()))
            .toList();
    }
}
