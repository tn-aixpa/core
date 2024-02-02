package it.smartcommunitylabdhub.core.services.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Set;

import it.smartcommunitylabdhub.core.models.entities.secret.Secret;

/**
 * Project secret service
 */
public interface ProjectSecretService {
    
    Secret createProjectSecret(Secret secret);

    Secret updateProjectSecret(Secret secret, String uuid);

    Secret getProjectSecret(String uuid);

    List<Secret> getProjectSecrets(String project);

    boolean deleteProjectSecret(String uuid);

    Map<String, String> getProjectSecretData(String project, Set<String> names);

    void storeProjectSecretData(String project, Map<String, String> values);
}
