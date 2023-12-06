package it.smartcommunitylabdhub.core.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.function.Function;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FunctionService {
    List<Function> getFunctions(Pageable pageable);

    List<Function> getFunctions();

    Function createFunction(Function functionDTO);

    Function getFunction(String uuid);

    Function updateFunction(Function functionDTO, String uuid);

    boolean deleteFunction(String uuid, Boolean cascade);

    List<Run> getFunctionRuns(String uuid);

    List<Function> getAllLatestFunctions();
}
