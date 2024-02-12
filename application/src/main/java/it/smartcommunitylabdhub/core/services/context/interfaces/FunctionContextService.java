package it.smartcommunitylabdhub.core.services.context.interfaces;

import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FunctionContextService {
    Function createFunction(String projectName, Function functionDTO);

    Function getByUuid(String projectName, String uuid);

    Page<Function> getByProjectNameAndFunctionName(
        Map<String, String> filter,
        String projectName,
        String functionName,
        Pageable pageable
    );

    List<Function> listByProjectNameAndFunctionName(String projectName, String functionName);

    Page<Function> getLatestByProjectName(Map<String, String> filter, String projectName, Pageable pageable);

    Function getByProjectAndFunctionAndUuid(String projectName, String functionName, String uuid);

    Function getLatestByProjectNameAndFunctionName(String projectName, String functionName);

    Function createOrUpdateFunction(String projectName, String functionName, Function functionDTO);

    Function updateFunction(String projectName, String functionName, String uuid, Function functionDTO);

    Boolean deleteSpecificFunctionVersion(String projectName, String functionName, String uuid);

    Boolean deleteAllFunctionVersions(String projectName, String functionName);
}
