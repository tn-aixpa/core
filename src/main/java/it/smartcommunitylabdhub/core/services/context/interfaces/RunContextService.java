package it.smartcommunitylabdhub.core.services.context.interfaces;

import it.smartcommunitylabdhub.core.models.entities.run.Run;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RunContextService {

    Run createRun(String projectName, Run runDTO);

    Page<Run> getAllRunsByProjectName(
            String projectName, Pageable pageable);

    Run getByProjectAndRunUuid(
            String projectName, String uuid);

    Run updateRun(String projectName, String uuid,
                  Run runDTO);

    Boolean deleteSpecificRunVersion(String projectName, String uuid);
}
