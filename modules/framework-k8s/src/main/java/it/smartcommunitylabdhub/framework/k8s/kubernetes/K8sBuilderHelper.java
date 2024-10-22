package it.smartcommunitylabdhub.framework.k8s.kubernetes;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMapEnvSource;
import io.kubernetes.client.openapi.models.V1ConfigMapVolumeSource;
import io.kubernetes.client.openapi.models.V1EmptyDirVolumeSource;
import io.kubernetes.client.openapi.models.V1EnvFromSource;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1EnvVarSource;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaimVolumeSource;
import io.kubernetes.client.openapi.models.V1SecretEnvSource;
import io.kubernetes.client.openapi.models.V1SecretKeySelector;
import io.kubernetes.client.openapi.models.V1Volume;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreVolume;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Helper class for building Kubernetes  environment variables.
 * This class provides methods to retrieve environment variables with fallback values
 * and constructs a list of V1EnvVar objects for use in Kubernetes  specifications.
 */
@Slf4j
@Component
@ConditionalOnKubernetes
public class K8sBuilderHelper implements InitializingBean {

    public static final int K8S_NAME_MAX_LENGTH = 62;
    public static final int TAG_LENGTH = 5;

    @Autowired
    ApiClient apiClient;

    @Autowired
    ApplicationProperties applicationProperties;

    @Value("${application.endpoint}")
    private String coreEndpoint;

    @Value("${kubernetes.config.secret}")
    private List<String> sharedSecrets;

    @Value("${kubernetes.config.config-map}")
    private List<String> sharedConfigMaps;

    @Value("${kubernetes.namespace}")
    private String namespace;

    @Value("${kubernetes.envs.prefix}")
    private String envsPrefix;

    @Override
    public void afterPropertiesSet() {
        // Retrieve CoreV1Api
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);

        // Remove all not present secrets
        sharedSecrets =
            sharedSecrets
                .stream()
                .flatMap(secret -> {
                    try {
                        coreV1Api.readNamespacedSecret(secret, namespace, null);
                        return Stream.of(secret);
                    } catch (ApiException e) {
                        log.error("Error reading secret: " + secret, e);
                        return Stream.empty();
                    }
                })
                .toList();

        // Remove all not present config maps
        sharedConfigMaps =
            sharedConfigMaps
                .stream()
                .flatMap(configMap -> {
                    try {
                        coreV1Api.readNamespacedConfigMap(configMap, namespace, null);
                        return Stream.of(configMap);
                    } catch (ApiException e) {
                        log.error("Error reading configmap: " + configMap, e);
                        return Stream.empty();
                    }
                })
                .toList();
    }

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
        List<V1EnvVar> vars = new ArrayList<>();
        //if no configMap build a minimal config
        if (sharedConfigMaps == null || sharedConfigMaps.isEmpty()) {
            vars.add(new V1EnvVar().name(sanitizeNames(envsPrefix).toUpperCase() + "_ENDPOINT").value(coreEndpoint));
        }

        return vars;
    }

    /**
     * Method to retrieve a list of V1EnvFromSource containing environment variables for a Kubernetes .
     * It retrieve env from Config Map and Secret
     *
     * @return A list of V1EnvVar objects representing environment variables for a Kubernetes .
     */

    public List<V1EnvFromSource> getV1EnvFromSource() {
        // Get Env var from secret and config map
        return Stream
            .concat(
                sharedConfigMaps
                    .stream()
                    .map(value -> new V1EnvFromSource().configMapRef(new V1ConfigMapEnvSource().name(value))),
                sharedSecrets
                    .stream()
                    //.filter(secret -> !secret.equals("")) // skip postgres
                    .map(secret -> new V1EnvFromSource().secretRef(new V1SecretEnvSource().name(secret)))
            )
            .toList();
    }

    public List<V1EnvVar> getEnvVarsFromSecrets(Map<String, Set<String>> secrets) {
        if (secrets != null) {
            return secrets
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .flatMap(entry ->
                    entry
                        .getValue()
                        .stream()
                        .map(key ->
                            new V1EnvVar()
                                .name(key)
                                .valueFrom(
                                    new V1EnvVarSource()
                                        .secretKeyRef(new V1SecretKeySelector().name(entry.getKey()).key(key))
                                )
                        )
                )
                .toList();
        }
        return Collections.emptyList();
    }

    public V1Volume getVolume(CoreVolume coreVolume) {
        V1Volume volume = new V1Volume().name(coreVolume.getName());
        String type = coreVolume.getVolumeType().name();
        Map<String, String> spec = coreVolume.getSpec();
        switch (type) {
            // TODO: support items
            case "config_map":
                return volume.configMap(
                    new V1ConfigMapVolumeSource().name(spec.getOrDefault("name", coreVolume.getName()))
                );
            //DISABLED: users should not be able to mount arbitrary secrets
            // case "secret":
            //     CoreItems coreItems = JacksonMapper.OBJECT_MAPPER.convertValue(
            //         spec.getOrDefault("items", new HashMap<>()),
            //         CoreItems.class
            //     );
            //     return volume.secret(
            //         new V1SecretVolumeSource()
            //             .secretName((String) spec.getOrDefault("secret_name", coreVolume.getName()))
            //             .items(
            //                 coreItems
            //                     .getCoreItems()
            //                     .stream()
            //                     .flatMap(map -> map.entrySet().stream())
            //                     .map(entry -> new V1KeyToPath().key(entry.getKey()).path((String) entry.getValue()))
            //                     .toList()
            //             )
            //     );
            case "persistent_volume_claim":
                return volume.persistentVolumeClaim(
                    new V1PersistentVolumeClaimVolumeSource()
                        .claimName(spec.getOrDefault("claimName", coreVolume.getName()))
                );
            case "empty_dir":
                return volume.emptyDir(
                    new V1EmptyDirVolumeSource()
                        .medium(spec.getOrDefault("medium", null))
                        .sizeLimit(Quantity.fromString(spec.getOrDefault("sizeLimit", "128Mi")))
                );
            default:
                return null;
        }
    }

    public Map<String, Quantity> convertResources(Map<String, String> map) {
        return map
            .entrySet()
            .stream()
            .map(entry -> Map.entry(entry.getKey(), Quantity.fromString(entry.getValue())))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    public V1VolumeMount getVolumeMount(CoreVolume coreVolume) {
        return new V1VolumeMount().name(coreVolume.getName()).mountPath(coreVolume.getMountPath());
    }

    /*
     * Helpers
     */
    //TODO align names!
    // Generate and return container name
    public String getContainerName(String runtime, String task, String id) {
        return sanitizeNames("c" + "-" + task + "-" + id);
    }

    // Generate and return job name
    public String getJobName(String runtime, String task, String id) {
        return sanitizeNames("j" + "-" + task + "-" + id);
    }

    // Generate and return deployment name
    public String getDeploymentName(String runtime, String task, String id) {
        return sanitizeNames("d" + "-" + task + "-" + id);
    }

    // Generate and return service name
    public String getServiceName(String runtime, String task, String id) {
        return sanitizeNames("s" + "-" + task + "-" + id);
    }

    public String getImageName(String image, String id) {
        String tag = "latest";
        if (StringUtils.hasText(id)) {
            tag = sanitizeNames(id.substring(0, Math.min(TAG_LENGTH, id.length())));
        }

        return image + ":" + tag;
    }

    public String getLabelName(String name) {
        return sanitizeNames(applicationProperties.getName()) + "/" + sanitizeNames(name);
    }

    public static String sanitizeNames(String name) {
        //sanitize value
        if (name == null) {
            return null;
        } else {
            //use only allowed chars in k8s resource names!
            String value = name.toLowerCase().replaceAll("[^a-zA-Z0-9._-]+", "");
            if (value.length() > K8S_NAME_MAX_LENGTH) {
                log.error("Name exceeds max length: {} ({})", String.valueOf(value.length()), value);

                throw new IllegalArgumentException(
                    "Name exceeds max length: " + String.valueOf(value.length()) + "(" + value + ")"
                );
                //DISABLED: multiple sanitizations should return the *same* value
                // return (
                //     value.substring(0, K8S_NAME_MAX_LENGTH - 7) +
                //     "-" +
                //     RandomStringUtils.randomAlphabetic(5).toLowerCase()
                // );
            }

            return value;
        }
    }
}
