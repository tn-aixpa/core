package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FunctionService {
    Page<Function> getFunctions(Map<String, String> filter, Pageable pageable);

    List<Function> getFunctions();

    Function createFunction(Function functionDTO);

    Function getFunction(String uuid);

    Function updateFunction(Function functionDTO, String uuid);

    boolean deleteFunction(String uuid, Boolean cascade);

    List<Run> getFunctionRuns(String uuid);

    List<Function> getAllLatestFunctions();
}
