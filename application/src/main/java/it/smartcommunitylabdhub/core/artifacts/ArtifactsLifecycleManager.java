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

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.artifact.Artifact;
import it.smartcommunitylabdhub.commons.services.ArtifactService;
import it.smartcommunitylabdhub.core.artifacts.lifecycle.LifecycleEvent;
import it.smartcommunitylabdhub.core.artifacts.lifecycle.ArtifactFsmFactory;
import it.smartcommunitylabdhub.core.artifacts.lifecycle.ArtifactLifecycleContext;
import it.smartcommunitylabdhub.core.artifacts.lifecycle.ArtifactLifecycleEvent;
import it.smartcommunitylabdhub.core.artifacts.lifecycle.ArtifactState;
import it.smartcommunitylabdhub.core.artifacts.specs.ArtifactBaseStatus;
import it.smartcommunitylabdhub.core.models.entities.ArtifactEntity;
import it.smartcommunitylabdhub.fsm.Fsm;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ArtifactsLifecycleManager
    extends BaseLifecycleManager<
        Artifact,
        ArtifactEntity,
        ArtifactBaseStatus,
        ArtifactState,
        ArtifactLifecycleEvent,
        ArtifactLifecycleContext,
        LifecycleEvent
    > {

    @Autowired
    private ArtifactFsmFactory fsmFactory;

    @Autowired
    private ArtifactService artifactService;

    @Override
    protected Fsm<ArtifactState, ArtifactLifecycleEvent, ArtifactLifecycleContext, LifecycleEvent> fsm(
        Artifact artifact
    ) {
        //initial state is current state
        ArtifactState initialState = ArtifactState.valueOf(StatusFieldAccessor.with(artifact.getStatus()).getState());
        ArtifactLifecycleContext context = ArtifactLifecycleContext.builder().artifact(artifact).build();

        // create state machine via factory
        return fsmFactory.create(initialState, context);
    }

    /*
     * Actions: ask to perform
     */

    public Artifact upload(@NotNull Artifact artifact) {
        return perform(artifact, ArtifactLifecycleEvent.UPLOAD);
    }

    public Artifact delete(@NotNull Artifact artifact) {
        return perform(artifact, ArtifactLifecycleEvent.DELETE);
    }

    public Artifact update(@NotNull Artifact artifact) {
        return perform(artifact, ArtifactLifecycleEvent.UPDATE);
    }

    /*
     * Events: callbacks
     */

    public void onReady(@NotNull Artifact artifact) {
        perform(artifact, ArtifactLifecycleEvent.READY);
    }

    public void onError(@NotNull Artifact artifact) {
        perform(artifact, ArtifactLifecycleEvent.ERROR);
    }
}
