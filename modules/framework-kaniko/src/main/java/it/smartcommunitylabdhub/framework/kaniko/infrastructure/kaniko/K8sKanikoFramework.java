package it.smartcommunitylabdhub.framework.kaniko.infrastructure.kaniko;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.*;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.FrameworkComponent;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sBaseFramework;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sJobFramework;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreVolume;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.framework.kaniko.runnables.ContextRef;
import it.smartcommunitylabdhub.framework.kaniko.runnables.ContextSource;
import it.smartcommunitylabdhub.framework.kaniko.runnables.K8sKanikoRunnable;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

@Slf4j
@FrameworkComponent(framework = K8sKanikoFramework.FRAMEWORK)
public class K8sKanikoFramework extends K8sBaseFramework<K8sKanikoRunnable, V1Job> {

    public static final String FRAMEWORK = "k8sbuild";
    private final BatchV1Api batchV1Api;

    @Value("${runtime.kaniko.image}")
    private String kanikoImage;

    @Value("${runtime.kaniko.init-image}")
    private String initImage;

    @Value("${runtime.kaniko.image-prefix}")
    private String imagePrefix;

    @Value("${runtime.kaniko.image-registry}")
    private String imageRegistry;

    @Value("${runtime.kaniko.credentials}")
    private String kanikoSecret;

    @Value("${runtime.kaniko.kaniko-args}")
    private List<String> kanikoArgs;


    @Autowired
    private K8sJobFramework jobFramework;


    public K8sKanikoFramework(ApiClient apiClient) {
        super(apiClient);
        this.batchV1Api = new BatchV1Api(apiClient);
    }

    // TODO: instead of void define a Result object that have to be merged with the run from the
    // caller.
    @Override
    public K8sKanikoRunnable run(K8sKanikoRunnable runnable) throws K8sFrameworkException {
        V1Job job = build(runnable);
        job = create(job);

        // Update runnable state..
        runnable.setState(State.RUNNING.name());

        return runnable;
    }

    @Override
    public K8sKanikoRunnable stop(K8sKanikoRunnable runnable) throws K8sFrameworkException {
        V1Job job = get(build(runnable));

        //stop by deleting
        delete(job);
        runnable.setState(State.STOPPED.name());

        return runnable;
    }

    @Override
    public K8sKanikoRunnable delete(K8sKanikoRunnable runnable) throws K8sFrameworkException {
        V1Job job;
        try {
            job = get(build(runnable));
        } catch (K8sFrameworkException e) {
            runnable.setState(State.DELETED.name());
            return runnable;
        }

        delete(job);
        runnable.setState(State.DELETED.name());

        return runnable;
    }

    @Override
    public V1Job build(K8sKanikoRunnable runnable) throws K8sFrameworkException {
        // Log service execution initiation
        log.info("----------------- BUILD KUBERNETES CRON JOB ----------------");

        // Generate jobName and ContainerName
        String jobName = k8sBuilderHelper.getJobName(runnable.getRuntime(), runnable.getTask(), runnable.getId());

        //build labels
        Map<String, String> labels = buildLabels(runnable);

        // Create the Job metadata
        V1ObjectMeta metadata = new V1ObjectMeta().name(jobName).labels(labels);

        // Create sharedVolume
        CoreVolume sharedVolume = new CoreVolume(
                "empty_dir",
                "/shared",
                "shared-dir", Map.of("sizeLimit", "100Mi"));

        List<CoreVolume> volumes = new ArrayList<>();
        List<CoreVolume> runnableVolumesOpt = Optional.ofNullable(runnable.getVolumes()).orElseGet(List::of);
        // Check if runnable already contains shared-dir
        if (runnableVolumesOpt.stream().noneMatch(v -> "shared-dir".equals(v.name()))) {
            volumes.add(sharedVolume);
        }

        // Create config map volume
        CoreVolume configMapVolume = new CoreVolume(
                "config_map",
                "/init-config-map",
                "init-config-map",
                Map.of("name", "init-config-map-" + runnable.getId()));

        if (runnableVolumesOpt.stream().noneMatch(v -> "init-config-map".equals(v.name()))) {
            volumes.add(configMapVolume);
        }

        // Add secret for kaniko
        CoreVolume secretVolume = new CoreVolume(
                "secret",
                "/kaniko/.docker/config.json",
                kanikoSecret,
                Map.of()
        );
        if (runnableVolumesOpt.stream().noneMatch(v -> kanikoSecret.equals(v.name()))) {
            volumes.add(secretVolume);
        }

        //Add all volumes
        Optional.ofNullable(runnable.getVolumes()).ifPresentOrElse(
                volumes::addAll,
                () -> runnable.setVolumes(volumes));


        // Define the command
        StringBuilder command = new StringBuilder().append(
                "/kaniko/executor " +
                        "--dockerfile=/shared/Dockerfile " +
                        "--context=/shared " +
                        "--destination=" +
                        imageRegistry + "/" +
                        imagePrefix + "-" +
                        runnable.getImage() + ":" + runnable.getId());

        // Add Kaniko args
        kanikoArgs.forEach(k -> command.append(" ").append(k));


        K8sJobRunnable k8sJobRunnable = K8sJobRunnable
                .builder()
                .id(runnable.getId())
                .args(runnable.getArgs())
                .affinity(runnable.getAffinity())
                .backoffLimit(runnable.getBackoffLimit())
                .command(command.toString())
                .envs(runnable.getEnvs())
                .image(kanikoImage)
                .labels(runnable.getLabels())
                .nodeSelector(runnable.getNodeSelector())
                .project(runnable.getProject())
                .resources(runnable.getResources())
                .runtime(runnable.getRuntime())
                .secrets(runnable.getSecrets())
                .task(runnable.getTask())
                .tolerations(runnable.getTolerations())
                .volumes(runnable.getVolumes())
                .state(State.READY.name())
                .build();

        // Build the Job
        V1Job job = jobFramework.build(k8sJobRunnable);


        try {
            // Generate Config map
            Optional<List<ContextRef>> contextRefsOpt = Optional.ofNullable(runnable.getContextRefs());
            Optional<List<ContextSource>> contextSourcesOpt = Optional.ofNullable(runnable.getContextSources());
            V1ConfigMap configMap = new V1ConfigMap()
                    .metadata(new V1ObjectMeta()
                            .name("init-config-map-" + runnable.getId()))
                    .data(
                            MapUtils.mergeMultipleMaps(
                                    Map.of("runnable", runnable.getId(),
                                            "Dockerfile", runnable.getDockerFile()),

                                    // Generate context-refs.txt if exist
                                    contextRefsOpt.map(contextRefsList ->
                                            Map.of("context-refs.txt", contextRefsList.stream()
                                                    .map(v -> v.getProtocol() + "," + v.getDestination() + "," + v.getSource())
                                                    .collect(Collectors.joining("\n")))
                                    ).orElseGet(Map::of),

                                    // Generate context-sources.txt if exist
                                    contextSourcesOpt.map(contextSources ->
                                            contextSources.stream()
                                                    .collect(Collectors.toMap(
                                                            ContextSource::getName,
                                                            c -> Arrays.toString(Base64.getUrlDecoder().decode(c.getBase64()))
                                                    ))
                                    ).orElseGet(Map::of)
                            ));

            // Create ConfigMap
            coreV1Api.createNamespacedConfigMap(
                    namespace,
                    configMap,
                    null,
                    null,
                    null,
                    null);


            // Build the Init Container
            V1Container initContainer = new V1Container()
                    .name("init-container-" + runnable.getId())
                    .image(initImage)
                    .volumeMounts(

                            // Return a list of V1VolumeMount based on runnable.getVolumes()
                            runnable.getVolumes().stream()
                                    .map(v -> new V1VolumeMount()
                                            .name(v.name()).mountPath(v.mountPath()))
                                    .collect(Collectors.toList())
                    )
                    //TODO below execute a command that is a Go script
                    .command(List.of("/bin/sh", "-c", "while :; do echo 'Hit CTRL+C'; sleep 1; done"));


            Optional.ofNullable(job)
                    .map(V1Job::getSpec)
                    .map(V1JobSpec::getTemplate)
                    .map(V1PodTemplateSpec::getSpec)
                    .ifPresentOrElse(
                            podSpec -> {
                                List<V1Container> initContainers = podSpec.getInitContainers();
                                if (initContainers == null) {
                                    initContainers = new ArrayList<>();
                                    podSpec.setInitContainers(initContainers);
                                }
                                initContainers.add(initContainer);
                            },
                            () -> {
                                // Handle the case where job, spec, or template is null
                                // For example, you might want to log an error or throw an exception
                                log.error("One of the intermediate objects is null.");
                            }
                    );


            // Create a new job with updated metadata and spec.
            return new V1Job().metadata(metadata).spec(job.getSpec());
        } catch (ApiException e) {
            throw new K8sFrameworkException(e.getMessage());
        }

    }

    @Override
    public V1Job apply(@NotNull V1Job job) throws K8sFrameworkException {
        return job;
    }

    @Override
    public V1Job get(@NotNull V1Job job) throws K8sFrameworkException {
        Assert.notNull(job.getMetadata(), "metadata can not be null");

        try {
            // Log service execution initiation
            log.info("----------------- GET KUBERNETES CRON JOB ----------------");

            return batchV1Api.readNamespacedJob(job.getMetadata().getName(), namespace, null);
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
            // Log service execution initiation
            log.info("----------------- RUN KUBERNETES CRON JOB ----------------");

            //dispatch job via api
            V1Job createdJob = batchV1Api.createNamespacedJob(namespace, job, null, null, null, null);
            log.info("Job created: {}", Objects.requireNonNull(createdJob.getMetadata()).getName());
            return createdJob;
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
            // Log service execution initiation
            log.info("----------------- RUN KUBERNETES CRON JOB ----------------");

            batchV1Api.deleteNamespacedJob(
                    job.getMetadata().getName(),
                    namespace,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getResponseBody());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getResponseBody());
        }
    }
}
