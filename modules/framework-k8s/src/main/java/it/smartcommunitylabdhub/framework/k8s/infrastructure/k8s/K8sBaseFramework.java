package it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1EnvFromSource;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1LocalObjectReference;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1Toleration;
import io.kubernetes.client.openapi.models.V1Volume;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import it.smartcommunitylabdhub.commons.infrastructure.Framework;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.jackson.IntOrStringMixin;
import it.smartcommunitylabdhub.framework.k8s.kubernetes.K8sBuilderHelper;
import it.smartcommunitylabdhub.framework.k8s.kubernetes.K8sSecretHelper;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreLabel;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreNodeSelector;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreResource;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Slf4j
public abstract class K8sBaseFramework<T extends K8sRunnable, K extends KubernetesObject>
    implements Framework<T>, InitializingBean {

    //custom object mapper with mixIn for IntOrString
    protected static final ObjectMapper mapper = JacksonMapper.CUSTOM_OBJECT_MAPPER.addMixIn(
        IntOrString.class,
        IntOrStringMixin.class
    );

    protected final CoreV1Api coreV1Api;

    protected ApplicationProperties applicationProperties;

    protected String namespace;
    protected String registrySecret;

    protected String version;
    protected K8sBuilderHelper k8sBuilderHelper;
    protected K8sSecretHelper k8sSecretHelper;

    protected K8sBaseFramework(ApiClient apiClient) {
        Assert.notNull(apiClient, "k8s api client is required");
        coreV1Api = new CoreV1Api(apiClient);
    }

    @Autowired
    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Autowired
    public void setNamespace(@Value("${kubernetes.namespace}") String namespace) {
        this.namespace = namespace;
    }

    @Autowired
    public void setVersion(@Value("${application.version}") String version) {
        this.version = version;
    }

    @Autowired
    public void setRegistrySecret(@Value("${registry.secret}") String secret) {
        this.registrySecret = secret;
    }

    @Autowired
    public void setK8sBuilderHelper(K8sBuilderHelper k8sBuilderHelper) {
        this.k8sBuilderHelper = k8sBuilderHelper;
    }

    @Autowired
    public void setK8sSecretHelper(K8sSecretHelper k8sSecretHelper) {
        this.k8sSecretHelper = k8sSecretHelper;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(k8sBuilderHelper, "k8s helper is required");
        Assert.notNull(namespace, "k8s namespace required");
        Assert.notNull(version, "k8s version required");
    }

    /*
     * Framework methods
     */

    @Override
    public T stop(T runnable) throws K8sFrameworkException {
        if (runnable != null) {
            log.info("destroy objects for runnable {}", runnable.getId());
            //TODO collect or rebuild objects
            // destroy(runnable);

            runnable.setState(State.STOPPED.name());
        }
        return runnable;
    }

    /*
     * K8s methods
     */
    public abstract K build(T runnable) throws K8sFrameworkException;

    public abstract K apply(K object) throws K8sFrameworkException;

    public abstract K create(K object) throws K8sFrameworkException;

    public abstract K get(K object) throws K8sFrameworkException;

    public abstract void delete(K object) throws K8sFrameworkException;

    //TODO support sinceTime when implemented by api
    //https://github.com/kubernetes-client/java/issues/2648
    // public Map<String, String> logs(K object, @Nullable Long sinceTime) throws K8sFrameworkException {

    public Map<String, String> logs(K object) throws K8sFrameworkException {
        if (object == null || object.getMetadata() == null) {
            return null;
        }

        //fetch labels to select pods
        //assume pods have the same labels as their parent
        //can be overridden downstream
        String label = "app.kubernetes.io/instance";
        String labelValue = Optional.ofNullable(object.getMetadata().getLabels()).map(m -> m.get(label)).orElse(null);
        if (labelValue == null) {
            //no selectors available
            return null;
        }

        String labelSelector = "app.kubernetes.io/instance=" + labelValue;
        try {
            V1PodList pods = coreV1Api.listNamespacedPod(
                namespace,
                null,
                null,
                null,
                null,
                labelSelector,
                null,
                null,
                null,
                null,
                null,
                null
            );

            Map<String, String> logs = new HashMap<>();

            for (V1Pod p : pods.getItems()) {
                if (p.getMetadata() != null) {
                    String n = p.getMetadata().getName();
                    String l = coreV1Api.readNamespacedPodLog(
                        n,
                        namespace,
                        null,
                        Boolean.FALSE,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        Boolean.TRUE
                    );

                    logs.put(n, l);
                }
            }

            return logs;
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getMessage());
        }
    }

    /*
     * Builder helpers
     * TODO move to a base builder class
     */
    protected Map<String, String> buildLabels(T runnable) {
        // Create labels for job
        Map<String, String> labels = Map.of(
            "app.kubernetes.io/instance",
            applicationProperties.getName() + "-" + runnable.getId(),
            "app.kubernetes.io/version",
            runnable.getId(),
            "app.kubernetes.io/part-of",
            //TODO add function name in place of runId
            applicationProperties.getName() + "-" + runnable.getProject() + "-" + runnable.getId(),
            "app.kubernetes.io/managed-by",
            applicationProperties.getName()
        );
        if (runnable.getLabels() != null && !runnable.getLabels().isEmpty()) {
            labels = new HashMap<>(labels);
            for (CoreLabel l : runnable.getLabels()) labels.putIfAbsent(l.name(), l.value());
        }

        return labels;
    }

    @SuppressWarnings("null")
    protected List<V1EnvVar> buildEnv(T runnable) {
        //shared envs
        List<V1EnvVar> sharedEnvs = k8sBuilderHelper.getV1EnvVar();

        //secretd based envs
        List<V1EnvVar> secretEnvs = k8sBuilderHelper.geEnvVarsFromSecrets(runnable.getSecrets());

        //secrets
        V1Secret secret = buildRunSecret(runnable);
        List<V1EnvVar> runSecretEnvs = new LinkedList<>();
        if (secret != null && secret.getStringData() != null && !secret.getStringData().isEmpty()) {
            Map<String, Set<String>> runSecretKeys = Collections.singletonMap(
                secret.getMetadata().getName(),
                secret.getStringData().keySet()
            );
            runSecretEnvs.addAll(k8sBuilderHelper.geEnvVarsFromSecrets(runSecretKeys));
            runSecretEnvs.add(new V1EnvVar().name("DH_RUN_SECRET_NAME").value(secret.getMetadata().getName()));
        }

        // function specific envs
        List<V1EnvVar> functionEnvs = runnable
            .getEnvs()
            .stream()
            .map(env -> new V1EnvVar().name(env.name()).value(env.value()))
            .collect(Collectors.toList());

        //merge all avoiding duplicates
        Map<String, V1EnvVar> envs = new HashMap<>();
        sharedEnvs.forEach(e -> envs.putIfAbsent(e.getName(), e));
        secretEnvs.forEach(e -> envs.putIfAbsent(e.getName(), e));
        functionEnvs.forEach(e -> envs.putIfAbsent(e.getName(), e));
        runSecretEnvs.forEach(e -> envs.putIfAbsent(e.getName(), e));

        return envs.values().stream().toList();
    }

    protected List<V1EnvFromSource> buildEnvFrom(T runnable) {
        List<V1EnvFromSource> envVarsFromSource = k8sBuilderHelper.getV1EnvFromSource();
        return envVarsFromSource;
    }

    protected List<V1Volume> buildVolumes(T runnable) {
        // Volumes to attach to the pod based on the volume spec with the additional volume_type
        List<V1Volume> volumes = new LinkedList<>();
        if (runnable.getVolumes() != null) {
            runnable
                .getVolumes()
                .forEach(volumeMap -> {
                    V1Volume volume = k8sBuilderHelper.getVolume(volumeMap);
                    if (volume != null) {
                        volumes.add(volume);
                    }
                });
        }

        return volumes;
    }

    protected List<V1VolumeMount> buildVolumeMounts(T runnable) {
        // Volumes to attach to the pod based on the volume spec with the additional volume_type
        List<V1VolumeMount> volumeMounts = new LinkedList<>();
        if (runnable.getVolumes() != null) {
            runnable
                .getVolumes()
                .forEach(volumeMap -> {
                    V1VolumeMount mount = k8sBuilderHelper.getVolumeMount(volumeMap);
                    if (mount != null) {
                        volumeMounts.add(mount);
                    }
                });
        }

        return volumeMounts;
    }

    protected V1ResourceRequirements buildResources(T runnable) {
        V1ResourceRequirements resources = new V1ResourceRequirements();
        if (runnable.getResources() != null) {
            //translate requests and limits
            CoreResource res = runnable.getResources();
            Map<String, String> requests = new HashMap<>();
            Map<String, String> limits = new HashMap<>();

            //cpu
            Optional
                .ofNullable(res.getCpu())
                .ifPresent(cpu -> {
                    if (cpu.requests() != null) {
                        requests.put("cpu", cpu.requests());
                    }
                    if (cpu.limits() != null) {
                        limits.put("cpu", cpu.limits());
                    }
                });

            //mem
            Optional
                .ofNullable(res.getMem())
                .ifPresent(mem -> {
                    if (mem.requests() != null) {
                        requests.put("memory", mem.requests());
                    }
                    if (mem.limits() != null) {
                        limits.put("memory", mem.limits());
                    }
                });

            //TODO gpu
            Optional
                .ofNullable(res.getGpu())
                .ifPresent(cpu -> {
                    //TODO
                });
            resources.setRequests(k8sBuilderHelper.convertResources(requests));
            resources.setLimits(k8sBuilderHelper.convertResources(limits));
        }

        return resources;
    }

    protected @Nullable Map<String, String> buildNodeSelector(T runnable) {
        if (runnable.getNodeSelector() != null && !runnable.getNodeSelector().isEmpty()) {
            return runnable
                .getNodeSelector()
                .stream()
                .collect(Collectors.toMap(CoreNodeSelector::key, CoreNodeSelector::value));
        }

        return null;
    }

    protected @Nullable List<V1Toleration> buildTolerations(T runnable) {
        if (runnable.getTolerations() != null && !runnable.getTolerations().isEmpty()) {
            return runnable.getTolerations().stream().map(t -> t).collect(Collectors.toList());
        }

        return null;
    }

    protected List<String> buildCommand(T runnable) {
        return Optional.ofNullable(runnable.getCommand()).map(Collections::singletonList).orElse(null);
    }

    protected List<String> buildArgs(T runnable) {
        return Optional.ofNullable(runnable.getArgs()).map(Arrays::asList).orElse(null);
    }

    @Nullable
    protected List<V1LocalObjectReference> buildImagePullSecrets(T runnable) {
        //always include registry secret if defined
        return Optional
            .ofNullable(registrySecret)
            .map(s -> Collections.singletonList(new V1LocalObjectReference().name(registrySecret)))
            .orElse(null);
    }

    protected V1Secret buildRunSecret(T runnable) {
        if (runnable.getCredentials() != null) {
            V1Secret secret = k8sSecretHelper.convertAuthentication(
                k8sSecretHelper.getSecretName(runnable.getRuntime(), runnable.getTask(), runnable.getId()),
                runnable.getCredentials()
            );

            if (secret != null && secret.getMetadata() != null) {
                //attach labels
                Map<String, String> labels = buildLabels(runnable);
                labels
                    .entrySet()
                    .forEach(e -> {
                        secret.getMetadata().putLabelsItem(e.getKey(), e.getValue());
                    });
            }

            return secret;
        }

        return null;
    }

    @SuppressWarnings("null")
    protected void storeRunSecret(V1Secret secret) throws K8sFrameworkException {
        try {
            k8sSecretHelper.storeSecretData(secret.getMetadata().getName(), secret.getStringData());
        } catch (JsonProcessingException | ApiException e) {
            throw new K8sFrameworkException(e.getMessage());
        }
    }

    protected void cleanRunSecret(T runnable) {
        String secretName = k8sSecretHelper.getSecretName(runnable.getRuntime(), runnable.getTask(), runnable.getId());
        try {
            k8sSecretHelper.deleteSecret(secretName);
        } catch (ApiException e) {
            log.warn("Failed to delete secret {}", secretName, e);
        }
    }
}
