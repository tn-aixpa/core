package it.smartcommunitylabdhub.core.services.context.interfaces;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RunContextService {
    Run createRun(String projectName, Run runDTO) throws NoSuchEntityException, DuplicatedEntityException;

    Page<Run> getAllRunsByProjectName(Map<String, String> filter, String projectName, Pageable pageable);

    Run getByProjectAndRunUuid(String projectName, String uuid);

    Run updateRun(String projectName, String uuid, Run runDTO);

    Boolean deleteSpecificRunVersion(String projectName, String uuid);
}
