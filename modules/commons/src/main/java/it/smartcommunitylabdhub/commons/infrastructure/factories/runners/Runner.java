package it.smartcommunitylabdhub.commons.infrastructure.factories.runners;

import javax.validation.constraints.NotNull;

import it.smartcommunitylabdhub.commons.models.entities.run.Run;

/**
 * Prender il RunDTO e produce il Runnable
 */
public interface Runner {
  Runnable produce(@NotNull Run runDTO);
}
