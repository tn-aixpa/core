package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Project secret service
 */
public interface SecretService {
    /**
     * Create new project secret entity and store it in the database. Throw error if the operation cannot be performed.
     * @param secret
     * @return
     */
    Secret createProjectSecret(Secret secret);

    /**
     * Update the secret with the specified id. Throw error if not found or if the operation cannot be performed.
     * @param secret
     * @param id
     * @return
     */
    Secret updateProjectSecret(Secret secret, String id);

    /**
     * Retrieve the secret with the specified id. Throw error if not found
     * @param id
     * @return
     */
    Secret getProjectSecret(String id);

    /**
     * List all the project secrets for the project with the specified name
     * @param project
     * @return
     */
    List<Secret> getProjectSecrets(String project);

    /**
     * Delete the secret with the specified id. Throw error if not found or if the operation cannot be performed.
     * @param id
     * @return
     */
    boolean deleteProjectSecret(String id);

    /**
     * Retrieve the project secret values for the specified names of the project
     * @param projectName
     * @param names
     * @return
     */
    Map<String, String> getProjectSecretData(String projectName, Set<String> names);

    /**
     * Store the values for the project secrets. If the secret does not exist, it will be created.
     * @param projectName
     * @param values
     */
    void storeProjectSecretData(String projectName, Map<String, String> values);

    /**
     * Group the specifiedsecrets by secret name as stored in provider. Only Kubernetes provider is supported at this moment.
     * @param projectId
     * @param secrets
     * @return
     */
    //TODO move to runtimes, this logic is outside the service
    @Deprecated(forRemoval = true)
    Map<String, Set<String>> groupSecrets(String projectId, Collection<String> secrets);
}
