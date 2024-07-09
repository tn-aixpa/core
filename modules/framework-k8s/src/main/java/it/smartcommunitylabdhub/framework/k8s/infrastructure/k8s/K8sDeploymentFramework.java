package it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s;

import com.fasterxml.jackson.core.type.TypeReference;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1EnvFromSource;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1Volume;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.FrameworkComponent;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.model.ContextRef;
import it.smartcommunitylabdhub.framework.k8s.model.ContextSource;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreVolume;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sDeploymentRunnable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

@Slf4j
@FrameworkComponent(framework = K8sDeploymentFramework.FRAMEWORK)
public class K8sDeploymentFramework extends K8sBaseFramework<K8sDeploymentRunnable, V1Deployment> {

    public static final String FRAMEWORK = "k8sdeployment";

    private static final TypeReference<HashMap<String, Serializable>> typeRef = new TypeReference<
        HashMap<String, Serializable>
    >() {};
    private final AppsV1Api appsV1Api;

    @Value("${kaniko.init-image}")
    private String initImage;

    public K8sDeploymentFramework(ApiClient apiClient) {
        super(apiClient);
        appsV1Api = new AppsV1Api(apiClient);
    }

    @Override
    public K8sDeploymentRunnable run(K8sDeploymentRunnable runnable) throws K8sFrameworkException {
        log.info("run for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        V1Deployment deployment = build(runnable);

        //secrets
        V1Secret secret = buildRunSecret(runnable);
        if (secret != null) {
            storeRunSecret(secret);
        }

        //check context refs and build config
        if (runnable.getContextRefs() != null || runnable.getContextSources() != null) {
            //build and create configMap
            //TODO move to shared method
            try {
                // Generate Config map
                Optional<List<ContextRef>> contextRefsOpt = Optional.ofNullable(runnable.getContextRefs());
                Optional<List<ContextSource>> contextSourcesOpt = Optional.ofNullable(runnable.getContextSources());
                V1ConfigMap configMap = new V1ConfigMap()
                    .metadata(
                        new V1ObjectMeta().name("init-config-map-" + runnable.getId()).labels(buildLabels(runnable))
                    )
                    .data(
                        MapUtils.mergeMultipleMaps(
                            // Generate context-refs.txt if exist
                            contextRefsOpt
                                .map(contextRefsList ->
                                    Map.of(
                                        "context-refs.txt",
                                        contextRefsList
                                            .stream()
                                            .map(v ->
                                                v.getProtocol() + "," + v.getDestination() + "," + v.getSource() + "\n"
                                            )
                                            .collect(Collectors.joining(""))
                                    )
                                )
                                .orElseGet(Map::of),
                            // Generate context-sources.txt if exist
                            contextSourcesOpt
                                .map(contextSources ->
                                    contextSources
                                        .stream()
                                        .collect(
                                            Collectors.toMap(
                                                c ->
                                                    Base64
                                                        .getUrlEncoder()
                                                        .withoutPadding()
                                                        .encodeToString(c.getName().getBytes()),
                                                c ->
                                                    new String(
                                                        Base64.getDecoder().decode(c.getBase64()),
                                                        StandardCharsets.UTF_8
                                                    )
                                            )
                                        )
                                )
                                .orElseGet(Map::of),
                            contextSourcesOpt
                                .map(contextSources ->
                                    Map.of(
                                        "context-sources-map.txt",
                                        contextSources
                                            .stream()
                                            .map(c ->
                                                Base64
                                                    .getUrlEncoder()
                                                    .withoutPadding()
                                                    .encodeToString(c.getName().getBytes()) +
                                                "," +
                                                c.getName() +
                                                "\n"
                                            )
                                            .collect(Collectors.joining(""))
                                    )
                                )
                                .orElseGet(Map::of)
                        )
                    );

                coreV1Api.createNamespacedConfigMap(namespace, configMap, null, null, null, null);
            } catch (ApiException | NullPointerException e) {
                throw new K8sFrameworkException(e.getMessage());
            }
        }

        log.info("create deployment for {}", String.valueOf(deployment.getMetadata().getName()));
        deployment = create(deployment);

        //update state
        runnable.setState(State.RUNNING.name());

        //update results
        try {
            runnable.setResults(Map.of("deployment", mapper.convertValue(deployment, typeRef)));
        } catch (IllegalArgumentException e) {
            log.error("error reading k8s results: {}", e.getMessage());
        }

        if (log.isTraceEnabled()) {
            log.trace("result: {}", runnable);
        }

        return runnable;
    }

    @Override
    public K8sDeploymentRunnable delete(K8sDeploymentRunnable runnable) throws K8sFrameworkException {
        log.info("delete for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        V1Deployment deployment;
        try {
            deployment = get(build(runnable));
        } catch (K8sFrameworkException e) {
            runnable.setState(State.DELETED.name());
            return runnable;
        }

        log.info("delete deployment for {}", String.valueOf(deployment.getMetadata().getName()));
        delete(deployment);

        //secrets
        cleanRunSecret(runnable);

        //init config map
        try {
            String configMapName = "init-config-map-" + runnable.getId();
            V1ConfigMap initConfigMap = coreV1Api.readNamespacedConfigMap(configMapName, namespace, null);
            if (initConfigMap != null) {
                coreV1Api.deleteNamespacedConfigMap(configMapName, namespace, null, null, null, null, null, null);
            }
        } catch (ApiException | NullPointerException e) {
            //ignore, not existing or error
        }

        //update results
        try {
            runnable.setResults(Map.of("deployment", mapper.convertValue(deployment, typeRef)));
        } catch (IllegalArgumentException e) {
            log.error("error reading k8s results: {}", e.getMessage());
        }

        //update state
        runnable.setState(State.DELETED.name());

        if (log.isTraceEnabled()) {
            log.trace("result: {}", runnable);
        }

        return runnable;
    }

    @Override
    public K8sDeploymentRunnable stop(K8sDeploymentRunnable runnable) throws K8sFrameworkException {
        log.info("stop for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        V1Deployment deployment = get(build(runnable));

        //stop by setting replicas to 0
        deployment.getSpec().setReplicas(0);
        apply(deployment);

        //update results
        try {
            runnable.setResults(Map.of("deployment", mapper.convertValue(deployment, typeRef)));
        } catch (IllegalArgumentException e) {
            log.error("error reading k8s results: {}", e.getMessage());
        }

        //update state
        runnable.setState(State.STOPPED.name());

        if (log.isTraceEnabled()) {
            log.trace("result: {}", runnable);
        }

        return runnable;
    }

    @Override
    public V1Deployment build(K8sDeploymentRunnable runnable) throws K8sFrameworkException {
        log.debug("build for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        // Generate deploymentName and ContainerName
        String deploymentName = k8sBuilderHelper.getDeploymentName(
            runnable.getRuntime(),
            runnable.getTask(),
            runnable.getId()
        );
        String containerName = k8sBuilderHelper.getContainerName(
            runnable.getRuntime(),
            runnable.getTask(),
            runnable.getId()
        );

        log.debug("build k8s deployment for {}", deploymentName);

        // Create labels for job
        Map<String, String> labels = buildLabels(runnable);

        // Create the Deployment metadata
        V1ObjectMeta metadata = new V1ObjectMeta().name(deploymentName).labels(labels);

        // Prepare environment variables for the Kubernetes job
        List<V1EnvFromSource> envFrom = buildEnvFrom(runnable);
        List<V1EnvVar> env = buildEnv(runnable);

        // Volumes to attach to the pod based on the volume spec with the additional volume_type
        List<V1Volume> volumes = buildVolumes(runnable);
        List<V1VolumeMount> volumeMounts = buildVolumeMounts(runnable);

        // resources
        V1ResourceRequirements resources = buildResources(runnable);

        //command params
        List<String> command = buildCommand(runnable);
        List<String> args = buildArgs(runnable);

        //check if context build is required
        if (runnable.getContextRefs() != null || runnable.getContextSources() != null) {
            // Create sharedVolume
            CoreVolume sharedVolume = new CoreVolume(
                CoreVolume.VolumeType.empty_dir,
                "/shared",
                "shared-dir",
                Map.of("sizeLimit", "100Mi")
            );

            // Create config map volume
            CoreVolume configMapVolume = new CoreVolume(
                CoreVolume.VolumeType.config_map,
                "/init-config-map",
                "init-config-map",
                Map.of("name", "init-config-map-" + runnable.getId())
            );

            List<V1Volume> initVolumes = List.of(
                k8sBuilderHelper.getVolume(sharedVolume),
                k8sBuilderHelper.getVolume(configMapVolume)
            );
            List<V1VolumeMount> initVolumesMounts = List.of(
                k8sBuilderHelper.getVolumeMount(sharedVolume),
                k8sBuilderHelper.getVolumeMount(configMapVolume)
            );

            //add volume
            volumes = Stream.concat(buildVolumes(runnable).stream(), initVolumes.stream()).collect(Collectors.toList());
            volumeMounts =
                Stream
                    .concat(buildVolumeMounts(runnable).stream(), initVolumesMounts.stream())
                    .collect(Collectors.toList());
        }

        // Build Container
        V1Container container = new V1Container()
            .name(containerName)
            .image(runnable.getImage())
            .imagePullPolicy("Always")
            .imagePullPolicy("IfNotPresent")
            .command(command)
            .args(args)
            .resources(resources)
            .volumeMounts(volumeMounts)
            .envFrom(envFrom)
            .env(env);

        // Create a PodSpec for the container
        V1PodSpec podSpec = new V1PodSpec()
            .containers(Collections.singletonList(container))
            .nodeSelector(buildNodeSelector(runnable))
            .affinity(runnable.getAffinity())
            .tolerations(buildTolerations(runnable))
            .runtimeClassName(runnable.getRuntimeClass())
            .priorityClassName(runnable.getPriorityClass())
            .volumes(volumes)
            .restartPolicy("Always")
            .imagePullSecrets(buildImagePullSecrets(runnable));

        //check if context build is required
        if (runnable.getContextRefs() != null || runnable.getContextSources() != null) {
            // Add Init container to the PodTemplateSpec
            // Build the Init Container
            V1Container initContainer = new V1Container()
                .name("init-container-" + runnable.getId())
                .image(initImage)
                .volumeMounts(volumeMounts)
                .resources(resources)
                .env(env)
                .envFrom(envFrom)
                //TODO below execute a command that is a Go script
                .command(List.of("/bin/bash", "-c", "/app/builder-tool.sh"));

            podSpec.setInitContainers(Collections.singletonList(initContainer));
        }

        // Create a PodTemplateSpec with the PodSpec
        V1PodTemplateSpec podTemplateSpec = new V1PodTemplateSpec().metadata(metadata).spec(podSpec);

        int replicas = Optional.ofNullable(runnable.getReplicas()).orElse(1);

        // Create the JobSpec with the PodTemplateSpec
        V1DeploymentSpec deploymentSpec = new V1DeploymentSpec()
            .replicas(replicas)
            .selector(new V1LabelSelector().matchLabels(labels))
            .template(podTemplateSpec);

        // Create the V1Deployment object with metadata and JobSpec
        return new V1Deployment().metadata(metadata).spec(deploymentSpec);
    }

    /*
     * K8s
     */
    @Override
    public V1Deployment apply(@NotNull V1Deployment deployment) throws K8sFrameworkException {
        Assert.notNull(deployment.getMetadata(), "metadata can not be null");
        Assert.notNull(deployment.getSpec(), "spec can not be null");

        try {
            String deploymentName = deployment.getMetadata().getName();
            log.debug("update k8s deployment for {}", deploymentName);

            return appsV1Api.replaceNamespacedDeployment(deploymentName, namespace, deployment, null, null, null, null);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getMessage());
        }
    }

    @Override
    public V1Deployment get(@NotNull V1Deployment deployment) throws K8sFrameworkException {
        Assert.notNull(deployment.getMetadata(), "metadata can not be null");

        try {
            String deploymentName = deployment.getMetadata().getName();
            log.debug("get k8s deployment for {}", deploymentName);

            return appsV1Api.readNamespacedDeployment(deploymentName, namespace, null);
        } catch (ApiException e) {
            log.info("Error with k8s: {}", e.getResponseBody());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getResponseBody());
        }
    }

    @Override
    public V1Deployment create(V1Deployment deployment) throws K8sFrameworkException {
        Assert.notNull(deployment.getMetadata(), "metadata can not be null");
        try {
            String deploymentName = deployment.getMetadata().getName();
            log.debug("create k8s deployment for {}", deploymentName);

            return appsV1Api.createNamespacedDeployment(namespace, deployment, null, null, null, null);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getResponseBody());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getResponseBody());
        }
    }

    @Override
    public void delete(@NotNull V1Deployment deployment) throws K8sFrameworkException {
        Assert.notNull(deployment.getMetadata(), "metadata can not be null");
        try {
            String deploymentName = deployment.getMetadata().getName();
            log.debug("delete k8s deployment for {}", deploymentName);

            appsV1Api.deleteNamespacedDeployment(deploymentName, namespace, null, null, null, null, null, null);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getResponseBody());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getResponseBody());
        }
    }
}
