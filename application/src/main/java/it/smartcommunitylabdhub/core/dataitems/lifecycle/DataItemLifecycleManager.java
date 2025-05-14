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

package it.smartcommunitylabdhub.core.dataitems.lifecycle;

import it.smartcommunitylabdhub.commons.models.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.dataitems.persistence.DataItemEntity;
import it.smartcommunitylabdhub.core.dataitems.specs.DataItemBaseStatus;
import it.smartcommunitylabdhub.core.lifecycle.BaseLifecycleManager;
import it.smartcommunitylabdhub.core.lifecycle.LifecycleEvents;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataItemLifecycleManager extends BaseLifecycleManager<DataItem, DataItemEntity, DataItemBaseStatus> {

    /*
     * Actions: ask to perform
     */

    public DataItem upload(@NotNull DataItem dataItem) {
        return perform(dataItem, LifecycleEvents.UPLOAD);
    }

    public DataItem delete(@NotNull DataItem dataItem) {
        return perform(dataItem, LifecycleEvents.DELETE);
    }

    public DataItem update(@NotNull DataItem dataItem) {
        return perform(dataItem, LifecycleEvents.UPDATE);
    }

    /*
     * Events: callbacks
     */

    //NOTE: disabled, we can not handle onCreated because there is no state change
    // public DataItem onCreated(@NotNull DataItem dataItem) {
    //     return handle(dataItem, State.CREATED);
    // }

    public DataItem onUploading(@NotNull DataItem dataItem) {
        return handle(dataItem, State.UPLOADING);
    }

    public DataItem onReady(@NotNull DataItem dataItem) {
        return handle(dataItem, State.READY);
    }

    public DataItem onError(@NotNull DataItem dataItem) {
        return handle(dataItem, State.ERROR);
    }
}
