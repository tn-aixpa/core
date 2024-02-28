package it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s;

import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import it.smartcommunitylabdhub.commons.infrastructure.Framework;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.kubernetes.K8sBuilderHelper;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreLabel;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreNodeSelector;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreResource;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import java.util.*;
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

    protected final CoreV1Api coreV1Api;

    @Value("${kubernetes.namespace}")
    protected String namespace;

    @Value("${application.version}")
    protected String version;

    @Autowired
    K8sBuilderHelper k8sBuilderHelper;

    protected K8sBaseFramework(ApiClient apiClient) {
        Assert.notNull(apiClient, "k8s api client is required");
        coreV1Api = new CoreV1Api(apiClient);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(k8sBuilderHelper, "k8s helper is required");
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

    public abstract K get(K object) throws K8sFrameworkException;

    public void destroy(K object) throws K8sFrameworkException {
        throw new UnsupportedOperationException();
    }

    /*
     * Builder helpers
     * TODO move to a base builder class
     */
    protected Map<String, String> buildLabels(T runnable) {
        // Create labels for job
        Map<String, String> labels = Map.of(
            "app.kubernetes.io/instance",
            "dhcore-" + runnable.getId(),
            "app.kubernetes.io/version",
            version,
            "app.kubernetes.io/component",
            "dhcore-k8s",
            "app.kubernetes.io/part-of",
            "dhcore",
            "app.kubernetes.io/managed-by",
            "dhcore"
        );
        if (runnable.getLabels() != null && !runnable.getLabels().isEmpty()) {
            labels = new HashMap<>(labels);
            for (CoreLabel l : runnable.getLabels()) labels.putIfAbsent(l.name(), l.value());
        }

        return labels;
    }

    protected List<V1EnvVar> buildEnv(T runnable) {
        //shared envs
        List<V1EnvVar> sharedEnvs = k8sBuilderHelper.getV1EnvVar();

        //secretd based envs
        List<V1EnvVar> secretEnvs = k8sBuilderHelper.geEnvVarsFromSecrets(runnable.getSecrets());

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
            resources.setRequests(
                k8sBuilderHelper.convertResources(
                    runnable
                        .getResources()
                        .stream()
                        .filter(r -> r.requests() != null)
                        .collect(Collectors.toMap(CoreResource::resourceType, CoreResource::requests))
                )
            );
            resources.setLimits(
                k8sBuilderHelper.convertResources(
                    runnable
                        .getResources()
                        .stream()
                        .filter(r -> r.limits() != null)
                        .collect(Collectors.toMap(CoreResource::resourceType, CoreResource::limits))
                )
            );
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
}
