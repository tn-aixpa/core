package it.smartcommunitylabdhub.core.components.kubernetes;

import io.kubernetes.client.openapi.models.V1ConfigMapEnvSource;
import io.kubernetes.client.openapi.models.V1EnvFromSource;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1SecretEnvSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Helper class for building Kubernetes job environment variables.
 * This class provides methods to retrieve environment variables with fallback values
 * and constructs a list of V1EnvVar objects for use in Kubernetes job specifications.
 */
@Component
public class K8sJobBuilderHelper {

    @Value("${application.endpoint}")
    private String DH_ENDPOINT;

    @Value("${kubernates.config.secret}")
    private List<String> SECRET;

    @Value("${kubernetes.config.config-map}")
    private List<String> CONFIG_MAP;


    /**
     * A helper method to get an environment variable with a default value if not present.
     *
     * @param variableName The name of the environment variable.
     * @param defaultValue The default value to use if the environment variable is not present.
     * @return The value of the environment variable if present, otherwise the defaultValue.
     */
    private String getEnvVariable(String variableName, String defaultValue) {
        // Access the environment variable using System.getenv()
        String value = System.getenv(variableName);

        System.getenv();
        // Use the value from the system environment if available, otherwise use the defaultValue
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }


    /**
     * Retrieve the dh end point variable
     *
     * @return V1EnvVar
     */
    public List<V1EnvVar> getV1EnvVar() {
//                v1EnvVars.add(
//                new V1EnvVar().name("DHUB_CORE_ENDPOINT")
//                        .value(DH_ENDPOINT));
        return new ArrayList<>();
    }

    /**
     * Method to retrieve a list of V1EnvFromSource containing environment variables for a Kubernetes job.
     * It retrieve env from Config Map and Secret
     *
     * @return A list of V1EnvVar objects representing environment variables for a Kubernetes job.
     */

    public List<V1EnvFromSource> getV1EnvFromSource() {

        // Get Env var from secret and config map
        return Stream.concat(
                CONFIG_MAP.stream().map(value -> new V1EnvFromSource().configMapRef(
                        new V1ConfigMapEnvSource().name(value)
                )),
                SECRET.stream().map(secret -> new V1EnvFromSource().secretRef(
                        new V1SecretEnvSource().name(secret)
                ))
        ).toList();

    }
}