package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FunctionService {
    Page<Function> getFunctions(Map<String, String> filter, Pageable pageable);

    // List<Function> getFunctions();

    Function createFunction(@NotNull Function functionDTO) throws DuplicatedEntityException;

    Function getFunction(@NotNull String id) throws NoSuchEntityException;

    Function updateFunction(@NotNull String id, @NotNull Function functionDTO) throws NoSuchEntityException;

    void deleteFunction(@NotNull String id, Boolean cascade);

    @Deprecated
    List<Run> getFunctionRuns(@NotNull String id) throws NoSuchEntityException;
    // List<Function> getAllLatestFunctions();
}
