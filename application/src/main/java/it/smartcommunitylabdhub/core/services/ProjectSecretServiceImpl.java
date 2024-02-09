package it.smartcommunitylabdhub.core.services;

import io.kubernetes.client.openapi.ApiException;
import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.exceptions.CustomException;
import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import it.smartcommunitylabdhub.commons.models.entities.secret.metadata.SecretMetadata;
import it.smartcommunitylabdhub.commons.models.entities.secret.specs.SecretBaseSpec;
import it.smartcommunitylabdhub.commons.services.interfaces.ProjectSecretService;
import it.smartcommunitylabdhub.commons.utils.ErrorList;
import it.smartcommunitylabdhub.core.models.builders.secret.SecretDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.secret.SecretEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;
import it.smartcommunitylabdhub.core.repositories.ProjectRepository;
import it.smartcommunitylabdhub.core.repositories.SecretRepository;
import it.smartcommunitylabdhub.framework.k8s.kubernetes.K8sSecretHelper;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProjectSecretServiceImpl implements ProjectSecretService {

  private static final String K8S_PROVIDER = "kubernetes";
  private static final String PATH_FORMAT = "%s://%s/%s";
  private static final Pattern PATH_PATTERN = Pattern.compile(
    "(\\w+)://([\\w-]+)/([\\w-]+)"
  );

  @Autowired
  SecretDTOBuilder secretDTOBuilder;

  @Autowired
  SecretEntityBuilder secretEntityBuilder;

  @Autowired(required = false)
  private K8sSecretHelper secretHelper;

  @Autowired
  private SecretRepository secretRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @Override
  public Secret createProjectSecret(Secret secretDTO) {
    // store in DB, do not create physically the secret
    if (
      secretDTO.getId() != null &&
      secretRepository.existsById(secretDTO.getId())
    ) {
      throw new CoreException(
        ErrorList.DUPLICATE_SECRET.getValue(),
        ErrorList.DUPLICATE_SECRET.getReason(),
        HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
    Optional<SecretEntity> savedSecret = Optional
      .of(secretDTO)
      .map(secretEntityBuilder::build)
      .map(this.secretRepository::saveAndFlush);

    return savedSecret
      .map(secret -> secretDTOBuilder.build(secret, false))
      .orElseThrow(() ->
        new CoreException(
          ErrorList.INTERNAL_SERVER_ERROR.getValue(),
          "Error saving secret",
          HttpStatus.INTERNAL_SERVER_ERROR
        )
      );
  }

  @Override
  public Secret updateProjectSecret(Secret secretDTO, String uuid) {
    if (!secretDTO.getId().equals(uuid)) {
      throw new CoreException(
        ErrorList.FUNCTION_NOT_MATCH.getValue(),
        ErrorList.FUNCTION_NOT_MATCH.getReason(),
        HttpStatus.NOT_FOUND
      );
    }

    final SecretEntity secret = secretRepository.findById(uuid).orElse(null);
    if (secret == null) {
      throw new CoreException(
        ErrorList.SECRET_NOT_FOUND.getValue(),
        ErrorList.SECRET_NOT_FOUND.getReason(),
        HttpStatus.NOT_FOUND
      );
    }

    try {
      final SecretEntity secretUpdated = secretEntityBuilder.update(
        secret,
        secretDTO
      );
      this.secretRepository.saveAndFlush(secretUpdated);

      return secretDTOBuilder.build(secretUpdated, false);
    } catch (CustomException e) {
      throw new CoreException(
        ErrorList.INTERNAL_SERVER_ERROR.getValue(),
        e.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }

  @Override
  public Secret getProjectSecret(String uuid) {
    final SecretEntity secret = secretRepository.findById(uuid).orElse(null);
    if (secret == null) {
      throw new CoreException(
        ErrorList.SECRET_NOT_FOUND.getValue(),
        ErrorList.SECRET_NOT_FOUND.getReason(),
        HttpStatus.NOT_FOUND
      );
    }

    try {
      return secretDTOBuilder.build(secret, false);
    } catch (CustomException e) {
      throw new CoreException(
        ErrorList.INTERNAL_SERVER_ERROR.getValue(),
        e.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }

  @Override
  public List<Secret> getProjectSecrets(String projectName) {
    return secretRepository
      .findByProject(projectName)
      .stream()
      .map(secretEntity -> {
        return secretDTOBuilder.build(secretEntity, false);
      })
      .collect(Collectors.toList());
  }

  @Override
  public boolean deleteProjectSecret(String uuid) {
    try {
      SecretEntity secret = secretRepository.findById(uuid).orElse(null);
      if (secret != null) {
        Secret secretDTO = secretDTOBuilder.build(secret, false);
        this.secretRepository.deleteById(uuid);
        if (secretHelper != null) {
          secretHelper.deleteSecretKeys(
            getProjectSecretName(secret.getProject()),
            Collections.singleton((String) secretDTO.getSpec().get("path"))
          );
        }
        return true;
      }
      throw new CoreException(
        ErrorList.SECRET_NOT_FOUND.getValue(),
        ErrorList.SECRET_NOT_FOUND.getReason(),
        HttpStatus.NOT_FOUND
      );
    } catch (Exception e) {
      throw new CoreException(
        ErrorList.INTERNAL_SERVER_ERROR.getValue(),
        "Cannot delete secret",
        HttpStatus.INTERNAL_SERVER_ERROR
      );
    }
  }

  @Override
  public Map<String, String> getProjectSecretData(
    String projectName,
    Set<String> names
  ) {
    if (names == null || names.isEmpty()) return Collections.emptyMap();

    Map<String, String> data = new HashMap<>();
    Map<String, String> secretData;
    if (secretHelper != null) {
      try {
        secretData =
          secretHelper.getSecretData(getProjectSecretName(projectName));
        if (secretData != null) {
          names.forEach(n -> {
            SecretEntity secret = secretRepository
              .findByProjectAndName(projectName, n)
              .orElse(null);
            if (secret != null) {
              data.put(n, secretData.get(n));
            }
          });
        }
      } catch (ApiException e) {
        throw new CoreException(
          ErrorList.INTERNAL_SERVER_ERROR.getValue(),
          "Cannot read secret",
          HttpStatus.INTERNAL_SERVER_ERROR
        );
      }
    }
    return data;
  }

  @Override
  public void storeProjectSecretData(
    String projectName,
    Map<String, String> values
  ) {
    if (values == null || values.isEmpty()) return;

    String secretName = getProjectSecretName(projectName);

    for (Entry<String, String> entry : values.entrySet()) {
      if (
        !secretRepository.existsByProjectAndName(projectName, entry.getKey())
      ) {
        Secret secret = new Secret();
        secret.setKind("secret");
        secret.setName(entry.getKey());
        secret.setProject(projectName);

        SecretMetadata secretMetadata = new SecretMetadata();
        secretMetadata.setCreated(new Date());
        secretMetadata.setEmbedded(true);
        secretMetadata.setName(entry.getKey());
        secretMetadata.setProject(projectName);
        secretMetadata.setUpdated(secretMetadata.getCreated());
        secret.setMetadata(secretMetadata);

        SecretBaseSpec spec = new SecretBaseSpec();
        spec.setProvider(K8S_PROVIDER);
        spec.setPath(getSecretPath(K8S_PROVIDER, secretName, entry.getKey()));
        secret.setSpec(spec.toMap());

        secretRepository.saveAndFlush(secretEntityBuilder.build(secret));
      }
    }
    if (secretHelper != null) {
      try {
        secretHelper.storeSecretData(secretName, values);
      } catch (Exception e) {
        throw new CoreException(
          ErrorList.INTERNAL_SERVER_ERROR.getValue(),
          "Cannot write secret",
          HttpStatus.INTERNAL_SERVER_ERROR
        );
      }
    }
  }

  /**
   * Group secrets by secret name as stored in provider. Only Kubernetes provider is supported at this moment.
   */
  @Override
  public Map<String, Set<String>> groupSecrets(
    String projectId,
    Collection<String> secrets
  ) {
    Optional<ProjectEntity> project = projectRepository.findById(projectId);
    if (project.isPresent()) {
      Map<String, Set<String>> result = new HashMap<>();
      if (secrets != null && !secrets.isEmpty()) {
        secretRepository
          .findByProject(project.get().getName())
          .stream()
          .filter(s -> secrets.contains(s.getName()))
          .forEach(secret -> {
            Secret secretDTO = secretDTOBuilder.build(secret, false);
            String path = (String) secretDTO.getSpec().get("path");
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
    return Map.of();
  }

  private String getProjectSecretName(String project) {
    return String.format("dhcore-proj-secrets-%s", project);
  }

  private String getSecretPath(String provider, String secret, String key) {
    return String.format(PATH_FORMAT, provider, secret, key);
  }
}
