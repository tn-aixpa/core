package it.smartcommunitylabdhub.core.services.context.interfaces;

import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SecretContextService {
    Secret createSecret(String projectName, Secret secretDTO);

    Page<Secret> getAllSecretsByProjectName(Map<String, String> filter, String projectName, Pageable pageable);

    Secret getByProjectAndSecretUuid(String projectName, String uuid);

    Secret updateSecret(String projectName, String uuid, Secret secretDTO);

    Boolean deleteSpecificSecretVersion(String projectName, String uuid);
}
