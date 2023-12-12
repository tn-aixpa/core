package it.smartcommunitylabdhub.core.services.context;

import it.smartcommunitylabdhub.core.components.fsm.enums.RunState;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.builders.run.RunDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.run.RunEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.filters.entities.RunEntityFilter;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.core.services.context.interfaces.RunContextService;
import it.smartcommunitylabdhub.core.services.interfaces.RunService;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class RunContextServiceImpl extends ContextService<RunEntity, RunEntityFilter> implements RunContextService {

    @Autowired
    RunRepository runRepository;

    @Autowired
    RunDTOBuilder runDTOBuilder;

    @Autowired
    RunEntityBuilder runEntityBuilder;

    @Autowired
    RunEntityFilter runEntityFilter;

    @Autowired
    RunService runService;

    @Override
    public Run createRun(String projectName, Run runDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // runDTO
            if (!projectName.equals(runDTO.getProject())) {
                throw new CustomException("Project Context and Run Project does not match",
                        null);
            }

            // Check project context
            checkContext(runDTO.getProject());

            return runService.createRun(runDTO);

        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public Page<Run> getAllRunsByProjectName(Map<String, String> filter, String projectName, Pageable pageable) {
        try {
            checkContext(projectName);

            runEntityFilter.setTask(filter.get("task"));
            runEntityFilter.setTaskId(filter.get("task_id"));
            runEntityFilter.setKind(filter.get("kind"));
            runEntityFilter.setCreatedDate(filter.get("created"));
            Optional<RunState> stateOptional = Stream.of(RunState.values())
                    .filter(state -> state.name().equals(filter.get("state")))
                    .findAny();
            runEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));


            Specification<RunEntity> specification = createSpecification(filter, runEntityFilter);

            Page<RunEntity> runPage = runRepository.findAll(
                    Specification.where(specification).and((root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(root.get("project"), projectName)), pageable);

            return new PageImpl<>(
                    runPage.getContent()
                            .stream()
                            .map(run -> runDTOBuilder.build(run))
                            .collect(Collectors.toList()),
                    pageable, runPage.getContent().size()
            );
        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public Run getByProjectAndRunUuid(String projectName,
                                      String uuid) {
        try {
            // Check project context
            checkContext(projectName);

            return this.runRepository
                    .findByProjectAndId(projectName, uuid).map(
                            run -> runDTOBuilder.build(run))
                    .orElseThrow(
                            () -> new CustomException(ErrorList.RUN_NOT_FOUND.getReason(),
                                    null));

        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public Run updateRun(String projectName,
                         String uuid,
                         Run runDTO) {

        try {
            // Check that project context is the same as the project passed to the
            // runDTO
            if (!projectName.equals(runDTO.getProject())) {
                throw new CustomException("Project Context and Run Project does not match",
                        null);
            }
            if (!uuid.equals(runDTO.getId())) {
                throw new CustomException(
                        "Trying to update a run with an ID different from the one passed in the request.",
                        null);
            }
            // Check project context
            checkContext(runDTO.getProject());

            RunEntity run = this.runRepository.findById(runDTO.getId()).map(
                            a -> // Update the existing run version
                                    runEntityBuilder.update(a, runDTO))
                    .orElseThrow(
                            () -> new CoreException(
                                    ErrorList.RUN_NOT_FOUND.getValue(),
                                    ErrorList.RUN_NOT_FOUND.getReason(),
                                    HttpStatus.INTERNAL_SERVER_ERROR
                            ));

            // Return run DTO
            return runDTOBuilder.build(run);

        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public Boolean deleteSpecificRunVersion(String projectName, String uuid) {
        try {
            if (this.runRepository.existsByProjectAndId(projectName, uuid)) {
                this.runRepository.deleteByProjectAndId(projectName, uuid);
                return true;
            }
            throw new CoreException(
                    ErrorList.RUN_NOT_FOUND.getValue(),
                    ErrorList.RUN_NOT_FOUND.getReason(),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "cannot delete run",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
