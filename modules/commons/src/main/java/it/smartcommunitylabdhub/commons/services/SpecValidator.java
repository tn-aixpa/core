package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.models.specs.Spec;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.BindException;

public interface SpecValidator {
    public void validateSpec(@NotNull Spec spec) throws BindException, IllegalArgumentException;
}
