package it.smartcommunitylabdhub.core.services.context;

import it.smartcommunitylabdhub.commons.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.abstracts.AbstractSpecificationService;
import it.smartcommunitylabdhub.core.models.queries.filters.interfaces.SpecificationFilter;
import it.smartcommunitylabdhub.core.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContextService<T, F extends SpecificationFilter<T>> extends AbstractSpecificationService<T, F> {

    @Autowired
    private ProjectRepository projectRepository;

    public ProjectEntity checkContext(String projectName) throws CustomException {
        return this.projectRepository.findByName(projectName)
            .orElseThrow(() -> new CustomException("(Context) Project " + "[" + projectName + "] not found", null));
    }
}
