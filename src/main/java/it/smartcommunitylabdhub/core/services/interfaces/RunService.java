package it.smartcommunitylabdhub.core.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface RunService {

    Page<Run> getRuns(Map<String, String> filter, Pageable pageable);

    Run getRun(String uuid);

    boolean deleteRun(String uuid, Boolean cascade);

    boolean deleteRunByTaskId(String uuid);

    Run save(Run runDTO);

    <F extends FunctionBaseSpec> Run createRun(Run inputRunDTO);

    Run updateRun(@Valid Run runDTO, String uuid);

}
