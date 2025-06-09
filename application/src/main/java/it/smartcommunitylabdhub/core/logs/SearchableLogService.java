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

package it.smartcommunitylabdhub.core.logs;

import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.log.Log;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.services.LogService;
import it.smartcommunitylabdhub.core.logs.persistence.LogEntity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/*
 * Searchable service for managing log
 */
public interface SearchableLogService extends LogService {
    /**
     * List all logs, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<Log> searchLogs(Pageable pageable, @Nullable SearchFilter<LogEntity> filter) throws SystemException;

    /**
     * List the latest version of every log, with optional filters
     * @param project
     * @param pageable
     * @param filter
     * @return
     */
    Page<Log> searchLogsByProject(@NotNull String project, Pageable pageable, @Nullable SearchFilter<LogEntity> filter)
        throws SystemException;
}
