package it.smartcommunitylabdhub.core.services.context;

import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import it.smartcommunitylabdhub.core.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContextService {

    @Autowired
    private ProjectRepository projectRepository;

    public ProjectEntity checkContext(String projectName) throws CustomException {

        return this.projectRepository.findByName(projectName)
                .orElseThrow(() -> new CustomException(
                        "(Context) Project " + "[" + projectName + "] not found", null));

    }

}
