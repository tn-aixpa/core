package it.smartcommunitylabdhub.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.kubernetes.client.openapi.ApiException;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import it.smartcommunitylabdhub.commons.models.entities.secret.SecretBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.secret.SecretMetadata;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.commons.services.entities.SecretService;
import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;
import it.smartcommunitylabdhub.core.models.entities.secret.specs.SecretSecretSpec;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
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
    SpecRegistry specRegistry;

    @Autowired(required = false)
    private K8sSecretHelper secretHelper;

    @Override
    public Page<Secret> listSecrets(Pageable pageable) {
        log.debug("list secrets page {}", pageable);

        return entityService.list(pageable);
    }

    @Override
    public List<Secret> listSecretsByProject(@NotNull String project) {
        log.debug("list secrets for project {}", project);
        Specification<SecretEntity> specification = Specification.allOf(CommonSpecification.projectEquals(project));

        return entityService.searchAll(specification);
    }

    @Override
    public Page<Secret> listSecretsByProject(@NotNull String project, Pageable pageable) {
        log.debug("list secrets for project {}", project);
        Specification<SecretEntity> specification = Specification.allOf(CommonSpecification.projectEquals(project));

        return entityService.search(specification, pageable);
    }

    @Override
    public Secret findSecret(@NotNull String id) {
        log.debug("find secret with id {}", String.valueOf(id));

        return entityService.find(id);
    }

    @Override
    public Secret getSecret(@NotNull String id) throws NoSuchEntityException {
        log.debug("get secret with id {}", String.valueOf(id));

        try {
            return entityService.get(id);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.SECRET.toString());
        }
    }

    @Override
    public Secret createSecret(@NotNull Secret dto) throws DuplicatedEntityException {
        log.debug("create secret");

        try {
            //parse base
            SecretBaseSpec secretSpec = new SecretBaseSpec();
            secretSpec.configure(dto.getSpec());

            String path = secretSpec.getPath();
            if (!StringUtils.hasText(path)) {
                throw new IllegalArgumentException("invalid or missing path in spec");
            }

            // Parse and export Spec
            Spec spec = specRegistry.createSpec(dto.getKind(), EntityName.SECRET, dto.getSpec());
            if (spec == null) {
                throw new IllegalArgumentException("invalid kind");
            }

            //TODO validate
            dto.setSpec(spec.toMap());

            // store in DB, do not create physically the secret
            return entityService.create(dto);
        } catch (DuplicatedEntityException e) {
            throw new DuplicatedEntityException(EntityName.SECRET.toString(), dto.getId());
        }
    }

    @Override
    public Secret updateSecret(@NotNull String id, @NotNull Secret dto) throws NoSuchEntityException {
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
        }
    }

    @Override
    public void deleteSecret(@NotNull String id) {
        log.debug("delete secret with id {}", String.valueOf(id));

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
    }

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

            Secret secret = entityService.searchAll(where).stream().findFirst().orElse(null);
            if (secret == null) {
                //store as new
                secret = new Secret();
                secret.setKind("secret");
                secret.setName(name);
                secret.setProject(project);

                SecretMetadata secretMetadata = new SecretMetadata();
                secretMetadata.setEmbedded(true);
                secret.setMetadata(secretMetadata.toMap());

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
