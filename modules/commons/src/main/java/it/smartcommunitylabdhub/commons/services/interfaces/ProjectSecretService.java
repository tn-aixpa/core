package it.smartcommunitylabdhub.commons.services.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;

/**
 * Project secret service
 */
public interface ProjectSecretService {
  /**
   * Create new project secret entity and store it in the database. Throw error if the operation cannot be performed.
   * @param secret
   * @return
   */
  Secret createProjectSecret(Secret secret);

  /**
   * Update the secret with the specified uuid. Throw error if not found or if the operation cannot be performed.
   * @param secret
   * @param uuid
   * @return
   */
  Secret updateProjectSecret(Secret secret, String uuid);

  /**
   * Retrieve the secret with the specified uuid. Throw error if not found
   * @param uuid
   * @return
   */
  Secret getProjectSecret(String uuid);

  /**
   * List all the project secrets for the project with the specified name
   * @param project
   * @return
   */
  List<Secret> getProjectSecrets(String project);

  /**
   * Delete the secret with the specified uuid. Throw error if not found or if the operation cannot be performed.
   * @param uuid
   * @return
   */
  boolean deleteProjectSecret(String uuid);

  /**
   * Retrieve the project secret values for the specified names of the project
   * @param projectName
   * @param names
   * @return
   */
  Map<String, String> getProjectSecretData(
    String projectName,
    Set<String> names
  );

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
  Map<String, Set<String>> groupSecrets(
    String projectId,
    Collection<String> secrets
  );
}
