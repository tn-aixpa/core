package it.smartcommunitylabdhub.core.services.context;

import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.exceptions.CustomException;
import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.utils.ErrorList;
import it.smartcommunitylabdhub.core.models.builders.secret.SecretDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.secret.SecretEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.SecretEntityFilter;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.core.repositories.SecretRepository;
import it.smartcommunitylabdhub.core.services.context.interfaces.SecretContextService;
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
public class SecretContextServiceImpl extends ContextService<SecretEntity, SecretEntityFilter> implements SecretContextService {

    @Autowired
    SecretRepository secretRepository;

    @Autowired
    SecretDTOBuilder secretDTOBuilder;

    @Autowired
    SecretEntityBuilder secretEntityBuilder;

    @Autowired
    SecretEntityFilter secretEntityFilter;

    @Autowired
    RunRepository runRepository;

    @Override
    public Secret createSecret(String projectName, Secret secretDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // secretDTO
            if (!projectName.equals(secretDTO.getProject())) {
                throw new CustomException("Project Context and Secret Project does not match", null);
            }

            // Check project context
            checkContext(secretDTO.getProject());

            // Check if secret already exist if exist throw exception otherwise create a
            // new one
            SecretEntity secret = (SecretEntity) Optional
                    .ofNullable(secretDTO.getId())
                    .flatMap(id ->
                            secretRepository
                                    .findById(id)
                                    .map(a -> {
                                        throw new CoreException(
                                                ErrorList.DUPLICATE_TASK.getValue(),
                                                ErrorList.DUPLICATE_TASK.getReason(),
                                                HttpStatus.INTERNAL_SERVER_ERROR
                                        );
                                    })
                    )
                    .orElseGet(() -> {
                        // Build a secret and store it in the database
                        SecretEntity newSecret = secretEntityBuilder.build(secretDTO);
                        return secretRepository.saveAndFlush(newSecret);
                    });

            // Return secret DTO
            return secretDTOBuilder.build(secret, secret.getEmbedded());
        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public Page<Secret> getAllSecretsByProjectName(Map<String, String> filter, String projectName, Pageable pageable) {
        try {
            checkContext(projectName);

            secretEntityFilter.setName(filter.get("name"));
            secretEntityFilter.setKind(filter.get("kind"));
            secretEntityFilter.setCreatedDate(filter.get("created"));
            Optional<State> stateOptional = Stream
                    .of(State.values())
                    .filter(state -> state.name().equals(filter.get("state")))
                    .findAny();
            secretEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

            Specification<SecretEntity> specification = createSpecification(filter, secretEntityFilter);

            Page<SecretEntity> secretPage = secretRepository.findAll(
                    Specification
                            .where(specification)
                            .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("project"), projectName)),
                    pageable
            );

            return new PageImpl<>(
                    secretPage.getContent().stream().map(secret -> secretDTOBuilder.build(secret, secret.getEmbedded())).collect(Collectors.toList()),
                    pageable,
                    secretPage.getTotalElements()
            );
        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public Secret getByProjectAndSecretUuid(String projectName, String uuid) {
        try {
            // Check project context
            checkContext(projectName);

            return this.secretRepository.findByProjectAndId(projectName, uuid)
                    .map(secret -> secretDTOBuilder.build(secret, secret.getEmbedded()))
                    .orElseThrow(() -> new CustomException(ErrorList.TASK_NOT_FOUND.getReason(), null));
        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public Secret updateSecret(String projectName, String uuid, Secret secretDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // secretDTO
            if (!projectName.equals(secretDTO.getProject())) {
                throw new CustomException("Project Context and Secret Project does not match", null);
            }
            if (!uuid.equals(secretDTO.getId())) {
                throw new CustomException(
                        "Trying to update a secret with an ID different from the one passed in the request.",
                        null
                );
            }
            // Check project context
            checkContext(secretDTO.getProject());

            SecretEntity secret =
                    this.secretRepository.findById(secretDTO.getId())
                            .map(a -> // Update the existing secret version
                                    secretEntityBuilder.update(a, secretDTO)
                            )
                            .orElseThrow(() ->
                                    new CoreException(
                                            ErrorList.TASK_NOT_FOUND.getValue(),
                                            ErrorList.TASK_NOT_FOUND.getReason(),
                                            HttpStatus.INTERNAL_SERVER_ERROR
                                    )
                            );

            // Return secret DTO
            return secretDTOBuilder.build(secret, secret.getEmbedded());
        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    @Transactional
    public Boolean deleteSpecificSecretVersion(String projectName, String uuid) {
        try {
            if (this.secretRepository.existsByProjectAndId(projectName, uuid)) {
                // Delete Secret
                this.secretRepository.deleteByProjectAndId(projectName, uuid);

                return true;
            }
            throw new CoreException(
                    ErrorList.TASK_NOT_FOUND.getValue(),
                    ErrorList.TASK_NOT_FOUND.getReason(),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "cannot delete secret",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
