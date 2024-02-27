package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

public interface RunService {
    Page<Run> getRuns(Map<String, String> filter, Pageable pageable);

    @Nullable
    Run findRun(@NotNull String id);

    Run getRun(@NotNull String id) throws NoSuchEntityException;

    Run createRun(@NotNull @Valid Run dto) throws NoSuchEntityException, DuplicatedEntityException;

    Run updateRun(@NotNull String id, @NotNull @Valid Run dto) throws NoSuchEntityException;

    void deleteRun(@NotNull String id, Boolean cascade);

    /*
     * Execution
     */
    Run buildRun(@NotNull @Valid Run dto);

    Run execRun(@NotNull @Valid Run dto);

    /*
     * Tasks
     */
    List<Run> getRunsByTask(@NotNull String task);
    void deleteRunsByTask(@NotNull String task);
}
