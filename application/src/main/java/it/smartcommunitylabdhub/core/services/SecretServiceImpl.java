package it.smartcommunitylabdhub.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.kubernetes.client.openapi.ApiException;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import it.smartcommunitylabdhub.commons.models.entities.secret.SecretBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.metadata.EmbeddableMetadata;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.commons.services.entities.SecretService;
import it.smartcommunitylabdhub.core.components.infrastructure.specs.SpecValidator;
import it.smartcommunitylabdhub.core.models.entities.ProjectEntity;
import it.smartcommunitylabdhub.core.models.entities.SecretEntity;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
import it.smartcommunitylabdhub.core.models.specs.secret.SecretSecretSpec;
import it.smartcommunitylabdhub.framework.k8s.kubernetes.K8sSecretHelper;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;

@Service
@Transactional
@Slf4j
public class SecretServiceImpl implements SecretService {

    private static final String K8S_PROVIDER = "kubernetes";
    private static final String PATH_FORMAT = "%s://%s/%s";
    private static final Pattern PATH_PATTERN = Pattern.compile("(\\w+)://([\\w-]+)/([\\w-]+)");

    @Autowired
    private EntityService<Secret, SecretEntity> entityService;

    @Autowired
    private EntityService<Project, ProjectEntity> projectService;

    @Autowired
    private SpecRegistry specRegistry;

    @Autowired
    private SpecValidator validator;

    @Autowired(required = false)
    private K8sSecretHelper secretHelper;

    @Override
    public Page<Secret> listSecrets(Pageable pageable) {
        log.debug("list secrets page {}", pageable);
        try {
            return entityService.list(pageable);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public List<Secret> listSecretsByUser(@NotNull String user) {
        log.debug("list all secrets for user {}  ", user);
        try {
            return entityService.searchAll(CommonSpecification.createdByEquals(user));
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public List<Secret> listSecretsByProject(@NotNull String project) {
        log.debug("list secrets for project {}", project);
        Specification<SecretEntity> specification = Specification.allOf(CommonSpecification.projectEquals(project));
        try {
            return entityService.searchAll(specification);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Page<Secret> listSecretsByProject(@NotNull String project, Pageable pageable) {
        log.debug("list secrets for project {}", project);
        Specification<SecretEntity> specification = Specification.allOf(CommonSpecification.projectEquals(project));
        try {
            return entityService.search(specification, pageable);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Secret findSecret(@NotNull String id) {
        log.debug("find secret with id {}", String.valueOf(id));
        try {
            return entityService.find(id);
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Secret getSecret(@NotNull String id) throws NoSuchEntityException {
        log.debug("get secret with id {}", String.valueOf(id));

        try {
            return entityService.get(id);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.SECRET.toString());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Secret createSecret(@NotNull Secret dto)
        throws DuplicatedEntityException, BindException, IllegalArgumentException {
        log.debug("create secret");
        try {
            //validate project
            String projectId = dto.getProject();
            if (!StringUtils.hasText(projectId) || projectService.find(projectId) == null) {
                throw new IllegalArgumentException("invalid or missing project");
            }

            try {
                //parse base
                SecretBaseSpec secretSpec = new SecretBaseSpec();
                secretSpec.configure(dto.getSpec());

                String path = secretSpec.getPath();
                if (!StringUtils.hasText(path)) {
                    throw new IllegalArgumentException("invalid or missing path in spec");
                }

                // Parse and export Spec
                Spec spec = specRegistry.createSpec(dto.getKind(), dto.getSpec());
                if (spec == null) {
                    throw new IllegalArgumentException("invalid kind");
                }

                //validate
                validator.validateSpec(spec);

                //update spec as exported
                dto.setSpec(spec.toMap());

                //check if a secret with this name already exists for the project
                Optional<Secret> existingSecret = listSecretsByProject(projectId)
                    .stream()
                    .filter(s -> s.getName().equals(dto.getName()))
                    .findFirst();
                if (existingSecret.isPresent()) {
                    throw new DuplicatedEntityException(EntityName.SECRET.toString(), dto.getName());
                }

                // store in DB, do not create physically the secret
                return entityService.create(dto);
            } catch (DuplicatedEntityException e) {
                throw new DuplicatedEntityException(EntityName.SECRET.toString(), dto.getId());
            }
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public Secret updateSecret(@NotNull String id, @NotNull Secret dto)
        throws NoSuchEntityException, BindException, IllegalArgumentException {
        log.debug("update secret with id {}", String.valueOf(id));
        try {
            //fetch current and merge
            Secret current = entityService.get(id);

            //keep spec
            dto.setSpec(current.getSpec());

            //update
            return entityService.update(id, dto);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.SECRET.toString());
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public void deleteSecret(@NotNull String id) {
        log.debug("delete secret with id {}", String.valueOf(id));
        try {
            Secret secret = findSecret(id);
            if (secret != null) {
                if (secretHelper != null) {
                    log.debug("cascade delete secret data for secret with id {}", String.valueOf(id));

                    try {
                        secretHelper.deleteSecretKeys(
                            getProjectSecretName(secret.getProject()),
                            //TODO use accessor for path
                            Collections.singleton((String) secret.getSpec().get("path"))
                        );
                    } catch (JsonProcessingException | ApiException e) {
                        log.error("error deleting secret data: {}", e.getMessage());
                        //TODO throw a dedicated error
                        throw new RuntimeException("error writing secrets");
                    }
                }

                //delete the secret
                entityService.delete(id);
            }
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    public void deleteSecretsByProject(@NotNull String project) {
        log.debug("delete secrets for project {}", project);
        try {
            //clear data first
            if (secretHelper != null) {
                try {
                    secretHelper.deleteSecret(getProjectSecretName(project));
                } catch (ApiException e) {
                    log.error("error deleting secret data for project {}:{}", project, e.getMessage());
                    throw new RuntimeException("error reading secrets");
                }
            }

            //delete entities
            entityService.deleteAll(CommonSpecification.projectEquals(project));
        } catch (StoreException e) {
            log.error("store error: {}", e.getMessage());
            throw new SystemException(e.getMessage());
        }
    }

    /*
     * Secret data (via provider)
     */
    @Override
    public Map<String, String> getSecretData(@NotNull String project, @NotNull Set<String> names) {
        if (names == null || names.isEmpty()) return Collections.emptyMap();

        if (secretHelper == null) {
            return Collections.emptyMap();
        }

        //fetch requested secrets
        List<Secret> secrets = listSecretsByProject(project)
            .stream()
            .filter(s -> names.contains(s.getName()))
            .collect(Collectors.toList());

        try {
            //unseal project secret data via provider
            Map<String, String> secretData = secretHelper.getSecretData(getProjectSecretName(project));
            if (secretData == null) {
                return Collections.emptyMap();
            }

            return secrets.stream().collect(Collectors.toMap(s -> s.getName(), s -> secretData.get(s.getName())));
        } catch (ApiException e) {
            log.error("error reading secret data for project {}:{}", project, e.getMessage());
            throw new RuntimeException("error reading secrets");
        }
    }

    @Override
    public void storeSecretData(@NotNull String project, @NotNull Map<String, String> values) {
        if (values == null || values.isEmpty()) return;

        String secretName = getProjectSecretName(project);

        for (Entry<String, String> entry : values.entrySet()) {
            String name = entry.getKey();

            //define a spec for tasks building function path
            Specification<SecretEntity> where = Specification.allOf(
                CommonSpecification.projectEquals(project),
                CommonSpecification.nameEquals(name)
            );
            try {
                Secret secret = entityService.searchAll(where).stream().findFirst().orElse(null);
                if (secret == null) {
                    //store as new
                    secret = new Secret();
                    secret.setKind("secret");
                    secret.setName(name);
                    secret.setProject(project);

                    //secrets are embedded by default
                    EmbeddableMetadata embeddableMetadata = new EmbeddableMetadata();
                    embeddableMetadata.setEmbedded(true);
                    secret.setMetadata(embeddableMetadata.toMap());

                    SecretBaseSpec spec = new SecretSecretSpec();
                    spec.setProvider(K8S_PROVIDER);
                    spec.setPath(getSecretPath(K8S_PROVIDER, secretName, entry.getKey()));
                    secret.setSpec(spec.toMap());

                    try {
                        entityService.create(secret);
                    } catch (DuplicatedEntityException e) {
                        //should not happen
                    }
                }
            } catch (StoreException e) {
                log.error("store error: {}", e.getMessage());
                throw new SystemException(e.getMessage());
            }
        }

        if (secretHelper != null) {
            try {
                secretHelper.storeSecretData(secretName, values);
            } catch (ApiException | JsonProcessingException e) {
                log.error("error writing secret data for project {}:{}", project, e.getMessage());
                throw new RuntimeException("error writing secrets");
            }
        }
    }

    /**
     * Group secrets by secret name as stored in provider. Only Kubernetes provider is supported at this moment.
     */
    @Override
    public Map<String, Set<String>> groupSecrets(String project, Collection<String> secrets) {
        Map<String, Set<String>> result = new HashMap<>();
        if (secrets != null && !secrets.isEmpty()) {
            listSecretsByProject(project)
                .stream()
                .filter(s -> secrets.contains(s.getName()))
                .forEach(secret -> {
                    String path = (String) secret.getSpec().get("path");
                    Matcher matcher = PATH_PATTERN.matcher(path);
                    if (matcher.matches()) {
                        String provider = matcher.group(1);
                        String secretName = matcher.group(2);
                        String key = matcher.group(3);
                        if (K8S_PROVIDER.equals(provider)) {
                            if (!result.containsKey(secretName)) {
                                result.put(secretName, new HashSet<>());
                            }
                            result.get(secretName).add(key);
                        }
                    }
                });
        }
        return result.isEmpty() ? Map.of() : result;
    }

    private String getProjectSecretName(String project) {
        return String.format("dhcore-proj-secrets-%s", project);
    }

    private String getSecretPath(String provider, String secret, String key) {
        return String.format(PATH_FORMAT, provider, secret, key);
    }
}
