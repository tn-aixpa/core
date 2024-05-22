package it.smartcommunitylabdhub.framework.kaniko.infrastructure.k8s;

import com.fasterxml.jackson.core.type.TypeReference;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1EnvFromSource;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1JobSpec;
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
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sBaseFramework;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreItems;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreVolume;
import it.smartcommunitylabdhub.framework.kaniko.runnables.ContextRef;
import it.smartcommunitylabdhub.framework.kaniko.runnables.ContextSource;
import it.smartcommunitylabdhub.framework.kaniko.runnables.K8sKanikoRunnable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Slf4j
@FrameworkComponent(framework = K8sKanikoFramework.FRAMEWORK)
public class K8sKanikoFramework extends K8sBaseFramework<K8sKanikoRunnable, V1Job> {

    public static final String FRAMEWORK = "k8sbuild";
    public static final int DEADLINE_SECONDS = 3600 * 24 * 3; //3 days
    private static final TypeReference<HashMap<String, Serializable>> typeRef = new TypeReference<
        HashMap<String, Serializable>
    >() {};

    private final BatchV1Api batchV1Api;

    private int activeDeadlineSeconds = DEADLINE_SECONDS;

    @Value("${kaniko.image}")
    private String kanikoImage;

    @Value("${kaniko.init-image}")
    private String initImage;

    @Value("${kaniko.image-prefix}")
    private String imagePrefix;

    @Value("${kaniko.image-registry}")
    private String imageRegistry;

    @Value("${kaniko.secret}")
    private String kanikoSecret;

    @Value("${kaniko.args}")
    private List<String> kanikoArgs;

    public K8sKanikoFramework(ApiClient apiClient) {
        super(apiClient);
        this.batchV1Api = new BatchV1Api(apiClient);
    }

    @Override
    public K8sKanikoRunnable run(K8sKanikoRunnable runnable) throws K8sFrameworkException {
        log.info("run for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        //create job
        V1Job job = build(runnable);

        //secrets
        V1Secret secret = buildRunSecret(runnable);
        if (secret != null) {
            storeRunSecret(secret);
        }

        log.info("create job for {}", String.valueOf(job.getMetadata().getName()));
        job = create(job);

        //update state
        runnable.setState(State.RUNNING.name());

        //update results
        try {
            runnable.setResults(Map.of("job", mapper.convertValue(job, typeRef)));
        } catch (IllegalArgumentException e) {
            log.error("error reading k8s results: {}", e.getMessage());
        }

        if (log.isTraceEnabled()) {
            log.trace("result: {}", runnable);
        }

        return runnable;
    }

    @Override
    public K8sKanikoRunnable stop(K8sKanikoRunnable runnable) throws K8sFrameworkException {
        log.info("stop for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        V1Job job = get(build(runnable));

        //stop by deleting
        log.info("delete job for {}", String.valueOf(job.getMetadata().getName()));
        delete(job);

        //update state
        runnable.setState(State.STOPPED.name());

        if (log.isTraceEnabled()) {
            log.trace("result: {}", runnable);
        }

        return runnable;
    }

    @Override
    public K8sKanikoRunnable delete(K8sKanikoRunnable runnable) throws K8sFrameworkException {
        log.info("delete for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        V1Job job;
        try {
            job = get(build(runnable));
        } catch (K8sFrameworkException e) {
            runnable.setState(State.DELETED.name());
            return runnable;
        }

        //secrets
        cleanRunSecret(runnable);

        log.info("delete job for {}", String.valueOf(job.getMetadata().getName()));
        delete(job);

        //update state
        runnable.setState(State.DELETED.name());

        if (log.isTraceEnabled()) {
            log.trace("result: {}", runnable);
        }

        return runnable;
    }

    @Override
    public V1Job build(K8sKanikoRunnable runnable) throws K8sFrameworkException {
        log.debug("build for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        // Generate jobName and ContainerName
        String jobName = k8sBuilderHelper.getJobName(runnable.getRuntime(), runnable.getTask(), runnable.getId());
        String containerName = k8sBuilderHelper.getContainerName(
            runnable.getRuntime(),
            runnable.getTask(),
            runnable.getId()
        );

        log.debug("build k8s job for {}", jobName);

        //build destination image name and set to runnable
        String prefix =
            (StringUtils.hasText(imageRegistry) ? imageRegistry + "/" : "") +
            (StringUtils.hasText(imagePrefix) ? imagePrefix + "-" : "");
        // workaround: update image name only first time
        String imageName = runnable.getImage();
        if (StringUtils.hasText(prefix) && !runnable.getImage().startsWith(prefix)) {
            imageName = k8sBuilderHelper.getImageName(prefix + runnable.getImage(), runnable.getId());
            runnable.setImage(imageName);
        }

        //build labels
        Map<String, String> labels = buildLabels(runnable);

        // Create the Job metadata
        V1ObjectMeta metadata = new V1ObjectMeta().name(jobName).labels(labels);

        // Create sharedVolume
        CoreVolume sharedVolume = new CoreVolume(
            CoreVolume.VolumeType.empty_dir,
            "/shared",
            "shared-dir",
            Map.of("sizeLimit", "100Mi")
        );

        List<CoreVolume> coreVolumes = new ArrayList<>();
        List<CoreVolume> runnableVolumesOpt = Optional.ofNullable(runnable.getVolumes()).orElseGet(List::of);
        // Check if runnable already contains shared-dir
        if (runnableVolumesOpt.stream().noneMatch(v -> "shared-dir".equals(v.getName()))) {
            coreVolumes.add(sharedVolume);
        }

        // Create config map volume
        CoreVolume configMapVolume = new CoreVolume(
            CoreVolume.VolumeType.config_map,
            "/init-config-map",
            "init-config-map",
            Map.of("name", "init-config-map-" + runnable.getId())
        );
        coreVolumes.add(configMapVolume);

        // Add secret for kaniko
        if (StringUtils.hasText(kanikoSecret)) {
            CoreVolume secretVolume = new CoreVolume(
                CoreVolume.VolumeType.secret,
                "/kaniko/.docker",
                kanikoSecret,
                Map.of("items", CoreItems.builder().keyToPath(Map.of(".dockerconfigjson", "config.json")).build())
            );
            if (runnableVolumesOpt.stream().noneMatch(v -> kanikoSecret.equals(v.getName()))) {
                coreVolumes.add(secretVolume);
            }
        }
        //Add all volumes
        Optional
            .ofNullable(runnable.getVolumes())
            .ifPresentOrElse(coreVolumes::addAll, () -> runnable.setVolumes(coreVolumes));

        List<String> kanikoArgsAll = new ArrayList<>(
            List.of("--dockerfile=/init-config-map/Dockerfile", "--context=/shared", "--destination=" + imageName)
        );
        // Add Kaniko args
        kanikoArgsAll.addAll(kanikoArgs);

        // Prepare environment variables for the Kubernetes job
        List<V1EnvFromSource> envFrom = buildEnvFrom(runnable);
        List<V1EnvVar> env = buildEnv(runnable);

        // Volumes to attach to the pod based on the volume spec with the additional volume_type
        List<V1Volume> volumes = buildVolumes(runnable);
        List<V1VolumeMount> volumeMounts = buildVolumeMounts(runnable);

        // resources
        V1ResourceRequirements resources = buildResources(runnable);

        // Build Container
        V1Container container = new V1Container()
            .name(containerName)
            .image(kanikoImage)
            .imagePullPolicy("Always")
            .imagePullPolicy("IfNotPresent")
            .args(kanikoArgsAll)
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
            .volumes(volumes)
            .restartPolicy("Never");

        // Create a PodTemplateSpec with the PodSpec
        V1PodTemplateSpec podTemplateSpec = new V1PodTemplateSpec().metadata(metadata).spec(podSpec);

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

        // Set initContainer as first container in the PodSpec
        podSpec.setInitContainers(Collections.singletonList(initContainer));

        int backoffLimit = Optional.ofNullable(runnable.getBackoffLimit()).orElse(0);

        // Create the JobSpec with the PodTemplateSpec
        V1JobSpec jobSpec = new V1JobSpec()
            .activeDeadlineSeconds(Long.valueOf(activeDeadlineSeconds))
            .parallelism(1)
            .completions(1)
            .backoffLimit(backoffLimit)
            .template(podTemplateSpec);

        try {
            // Generate Config map
            Optional<List<ContextRef>> contextRefsOpt = Optional.ofNullable(runnable.getContextRefs());
            Optional<List<ContextSource>> contextSourcesOpt = Optional.ofNullable(runnable.getContextSources());
            V1ConfigMap configMap = new V1ConfigMap()
                .metadata(new V1ObjectMeta().name("init-config-map-" + runnable.getId()).labels(labels))
                .data(
                    MapUtils.mergeMultipleMaps(
                        Map.of("Dockerfile", runnable.getDockerFile()),
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
                                            ContextSource::getName,
                                            c -> Arrays.toString(Base64.getUrlDecoder().decode(c.getBase64()))
                                        )
                                    )
                            )
                            .orElseGet(Map::of)
                    )
                );

            // Check if config map already exist. if not, create it
            //TODO move creation to run NOT build!
            try {
                coreV1Api.readNamespacedConfigMap(
                    Objects.requireNonNull(configMap.getMetadata()).getName(),
                    namespace,
                    null
                ); // ConfigMap already exist  -> do nothing
            } catch (ApiException e) { // ConfigMap does not exist -> create it
                coreV1Api.createNamespacedConfigMap(namespace, configMap, null, null, null, null);
            }

            // Return a new job with metadata and jobSpec
            return new V1Job().metadata(metadata).spec(jobSpec);
        } catch (ApiException | NullPointerException e) {
            throw new K8sFrameworkException(e.getMessage());
        }
    }

    /*
     * K8s
     */
    @Override
    public V1Job apply(@NotNull V1Job job) throws K8sFrameworkException {
        return job;
    }

    @Override
    public V1Job get(@NotNull V1Job job) throws K8sFrameworkException {
        Assert.notNull(job.getMetadata(), "metadata can not be null");

        try {
            String jobName = job.getMetadata().getName();
            log.debug("get k8s job for {}", jobName);

            return batchV1Api.readNamespacedJob(jobName, namespace, null);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getMessage());
        }
    }

    @Override
    public V1Job create(V1Job job) throws K8sFrameworkException {
        Assert.notNull(job.getMetadata(), "metadata can not be null");

        try {
            String jobName = job.getMetadata().getName();
            log.debug("create k8s job for {}", jobName);

            //dispatch job via api
            return batchV1Api.createNamespacedJob(namespace, job, null, null, null, null);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getResponseBody());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getResponseBody());
        }
    }

    @Override
    public void delete(V1Job job) throws K8sFrameworkException {
        Assert.notNull(job.getMetadata(), "metadata can not be null");

        try {
            String jobName = job.getMetadata().getName();
            log.debug("delete k8s job for {}", jobName);

            batchV1Api.deleteNamespacedJob(jobName, namespace, null, null, null, null, null, null);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getResponseBody());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getResponseBody());
        }
    }
}
