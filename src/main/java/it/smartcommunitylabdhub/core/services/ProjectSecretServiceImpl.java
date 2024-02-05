package it.smartcommunitylabdhub.core.services;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import io.kubernetes.client.openapi.ApiException;
import it.smartcommunitylabdhub.core.components.kubernetes.K8sSecretHelper;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.builders.secret.SecretDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.secret.SecretEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.secret.Secret;
import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;
import it.smartcommunitylabdhub.core.models.entities.secret.metadata.SecretMetadata;
import it.smartcommunitylabdhub.core.models.entities.secret.specs.SecretBaseSpec;
import it.smartcommunitylabdhub.core.repositories.SecretRepository;
import it.smartcommunitylabdhub.core.services.interfaces.ProjectSecretService;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProjectSecretServiceImpl implements ProjectSecretService {
    
    @Autowired
    private K8sSecretHelper secretHelper;

    @Autowired
    private SecretRepository secretRepository;

    @Autowired
    SecretDTOBuilder secretDTOBuilder;

    @Autowired
    SecretEntityBuilder secretEntityBuilder;

    private static final String K8S_PROVIDER = "kubernetes";

    @Override
    public Secret createProjectSecret(Secret secretDTO) {
        // store in DB, do not create physically the secret
        if (secretDTO.getId() != null && secretRepository.existsById(secretDTO.getId())) {
            throw new CoreException(
                    ErrorList.DUPLICATE_SECRET.getValue(),
                    ErrorList.DUPLICATE_SECRET.getReason(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Optional<SecretEntity> savedSecret = Optional.of(secretDTO)
                .map(secretEntityBuilder::build)
                .map(this.secretRepository::saveAndFlush);

        return savedSecret
                .map(secret -> secretDTOBuilder.build(secret, false))
                .orElseThrow(() -> new CoreException(
                        ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                        "Error saving secret",
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }


    @Override
    public Secret updateProjectSecret(Secret secretDTO, String uuid) {
        if (!secretDTO.getId().equals(uuid)) {
            throw new CoreException(
                    ErrorList.FUNCTION_NOT_MATCH.getValue(),
                    ErrorList.FUNCTION_NOT_MATCH.getReason(),
                    HttpStatus.NOT_FOUND);
        }

        final SecretEntity secret = secretRepository.findById(uuid).orElse(null);
        if (secret == null) {
            throw new CoreException(
                    ErrorList.SECRET_NOT_FOUND.getValue(),
                    ErrorList.SECRET_NOT_FOUND.getReason(),
                    HttpStatus.NOT_FOUND);
        }

        try {

            final SecretEntity secretUpdated = secretEntityBuilder.update(secret, secretDTO);
            this.secretRepository.saveAndFlush(secretUpdated);

            return secretDTOBuilder.build(secretUpdated, false);

        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }    }


    @Override
    public Secret getProjectSecret(String uuid) {
        final SecretEntity secret = secretRepository.findById(uuid).orElse(null);
        if (secret == null) {
            throw new CoreException(
                    ErrorList.SECRET_NOT_FOUND.getValue(),
                    ErrorList.SECRET_NOT_FOUND.getReason(),
                    HttpStatus.NOT_FOUND);
        }

        try {
            return secretDTOBuilder.build(secret, false);

        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public List<Secret> getProjectSecrets(String project) {
        return secretRepository.findByProject(project).stream().map(secretEntity -> {
            return secretDTOBuilder.build(secretEntity, false);
        }).collect(Collectors.toList());
    }


    @Override
    public boolean deleteProjectSecret(String uuid) {
        try {
            SecretEntity secret = secretRepository.findById(uuid).orElse(null);
            if (secret != null) {
                Secret secretDTO = secretDTOBuilder.build(secret, false);
                this.secretRepository.deleteById(uuid);
                secretHelper.deleteSecretKeys(getProjectSecretName(secret.getProject()), Collections.singleton((String)secretDTO.getSpec().get("path")));
                return true;
            }
            throw new CoreException(
                    ErrorList.SECRET_NOT_FOUND.getValue(),
                    ErrorList.SECRET_NOT_FOUND.getReason(),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "Cannot delete secret",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public Map<String, String> getProjectSecretData(String project, Set<String> names) {
        if (names == null || names.isEmpty()) return Collections.emptyMap();
        
        Map<String, String> data = new HashMap<>();
        Map<String, String> secretData;
        try {
            secretData = secretHelper.getSecretData(getProjectSecretName(project));
            if (secretData != null) {
                names.forEach(n -> {
                    SecretEntity secret = secretRepository.findByProjectAndName(project, n).orElse(null);
                    if (secret != null) {
                        Secret secretDTO = secretDTOBuilder.build(secret, false);
                        data.put(n, secretData.get((String)secretDTO.getSpec().get("path")));
                    }
                });
            }
            } catch (ApiException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "Cannot read secret",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return data;
    }


    @Override
    public void storeProjectSecretData(String project, Map<String, String> values) {
        if (values == null || values.isEmpty()) return;

        for (Entry<String,String> entry : values.entrySet()) {
            if (!secretRepository.existsByProjectAndName(project, entry.getKey())) {
                Secret secret = new Secret();
                secret.setKind("secret");
                secret.setName(entry.getKey());
                secret.setProject(project);
                
                SecretMetadata secretMetadata = new SecretMetadata();
                secretMetadata.setCreated(new Date());
                secretMetadata.setEmbedded(true);
                secretMetadata.setName(entry.getKey());
                secretMetadata.setProject(project);
                secretMetadata.setUpdated(secretMetadata.getCreated());
                secret.setMetadata(secretMetadata);

                SecretBaseSpec spec = new SecretBaseSpec();
                spec.setProvider(K8S_PROVIDER);
                spec.setPath(entry.getKey());
                secret.setSpec(spec.toMap());
                
                secretRepository.saveAndFlush(secretEntityBuilder.build(secret));
            }
        }
        try {
            secretHelper.storeSecretData(getProjectSecretName(project), values);
        } catch (Exception e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "Cannot write secret",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private String getProjectSecretName(String project) {
        return String.format("dhcore-proj-secrets-%s", project);
    }


}
