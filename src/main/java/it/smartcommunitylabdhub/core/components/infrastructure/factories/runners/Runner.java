package it.smartcommunitylabdhub.core.components.infrastructure.factories.runners;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.Runnable;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import jakarta.validation.constraints.NotNull;


/**
 * Prender il RunDTO e produce il Runnable
 */
public interface Runner {
    Runnable produce(@NotNull Run runDTO);
}
