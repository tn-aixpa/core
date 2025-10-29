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

package it.smartcommunitylabdhub.core.logs.filter;

import it.smartcommunitylabdhub.commons.models.log.Log;
import it.smartcommunitylabdhub.core.logs.persistence.LogEntity;
import it.smartcommunitylabdhub.core.queries.filters.AbstractEntityFilterConverter;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class LogFilterConverter extends AbstractEntityFilterConverter<Log, LogEntity> {

    @Override
    protected String map(@NotNull String source) {
        //run is top level
        if ("run".equals(source)) {
            return "run";
        }

        return super.map(source);
    }
}
