package it.smartcommunitylabdhub.core.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RunService {

    List<Run> getRuns(Pageable pageable);

    Run getRun(String uuid);

    boolean deleteRun(String uuid);

    Run save(Run runDTO);

    <F extends FunctionBaseSpec<F>> Run createRun(Run inputRunDTO);

    Run updateRun(@Valid Run runDTO, String uuid);

}
