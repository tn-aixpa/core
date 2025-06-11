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

package it.smartcommunitylabdhub.core.triggers.service;

import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.models.trigger.Trigger;
import it.smartcommunitylabdhub.commons.services.TriggerService;
import it.smartcommunitylabdhub.core.triggers.persistence.TriggerEntity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/*
 * Searchable service for managing trigger
 */
public interface SearchableTriggerService extends TriggerService {
    /**
     * List all triggers, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<Trigger> searchTriggers(Pageable pageable, @Nullable SearchFilter<TriggerEntity> filter)
        throws SystemException;

    /**
     * List the latest version of every trigger, with optional filters
     * @param project
     * @param pageable
     * @param filter
     * @return
     */
    Page<Trigger> searchTriggersByProject(
        @NotNull String project,
        Pageable pageable,
        @Nullable SearchFilter<TriggerEntity> filter
    ) throws SystemException;
}
