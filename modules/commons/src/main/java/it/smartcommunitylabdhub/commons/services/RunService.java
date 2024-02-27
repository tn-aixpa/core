package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RunService {
    Page<Run> getRuns(Map<String, String> filter, Pageable pageable);

    Run getRun(String uuid);

    boolean deleteRun(String uuid, Boolean cascade);

    boolean deleteRunByTaskId(String uuid);

    Run save(Run runDTO);

    <F extends FunctionBaseSpec> Run createRun(Run inputRunDTO);

    Run updateRun(@Valid Run runDTO, String uuid);
}
