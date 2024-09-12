package it.smartcommunitylabdhub.core.components.infrastructure.specs;

import it.smartcommunitylabdhub.commons.models.specs.Spec;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.MethodArgumentNotValidException;

public interface SpecValidator {
    public void validateSpec(@NotNull Spec spec) throws MethodArgumentNotValidException, IllegalArgumentException;
}
