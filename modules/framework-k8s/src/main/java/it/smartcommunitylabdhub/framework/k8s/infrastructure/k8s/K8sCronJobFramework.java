package it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s;

import com.fasterxml.jackson.core.type.TypeReference;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1CronJob;
import io.kubernetes.client.openapi.models.V1CronJobSpec;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1JobTemplateSpec;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Secret;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.FrameworkComponent;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sCronJobRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Slf4j
@FrameworkComponent(framework = K8sCronJobFramework.FRAMEWORK)
public class K8sCronJobFramework extends K8sBaseFramework<K8sCronJobRunnable, V1CronJob> {

    public static final String FRAMEWORK = "k8scronjob";

    private static final TypeReference<HashMap<String, Serializable>> typeRef = new TypeReference<
        HashMap<String, Serializable>
    >() {};
    private final BatchV1Api batchV1Api;

    //TODO refactor usage of framework: should split framework from infrastructure!
    private final K8sJobFramework jobFramework;

    public K8sCronJobFramework(ApiClient apiClient) {
        super(apiClient);
        this.batchV1Api = new BatchV1Api(apiClient);
        jobFramework = new K8sJobFramework(apiClient);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        //configure dependant framework
        this.jobFramework.setApplicationProperties(applicationProperties);
        this.jobFramework.setCollectLogs(collectLogs);
        this.jobFramework.setCollectMetrics(collectMetrics);
        this.jobFramework.setCpuResourceDefinition(cpuResourceDefinition);
        this.jobFramework.setDisableRoot(disableRoot);
        this.jobFramework.setImagePullPolicy(imagePullPolicy);
        this.jobFramework.setMemResourceDefinition(memResourceDefinition);
        this.jobFramework.setNamespace(namespace);
        this.jobFramework.setRegistrySecret(registrySecret);
        this.jobFramework.setVersion(version);
        this.jobFramework.setK8sBuilderHelper(k8sBuilderHelper);
        this.jobFramework.setK8sSecretHelper(k8sSecretHelper);
    }

    @Override
    public K8sCronJobRunnable run(K8sCronJobRunnable runnable) throws K8sFrameworkException {
        log.info("run for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        //create job
        V1CronJob job = build(runnable);

        //secrets
        V1Secret secret = buildRunSecret(runnable);
        if (secret != null) {
            storeRunSecret(secret);
        }

        try {
            V1ConfigMap initConfigMap = buildInitConfigMap(runnable);
            if (initConfigMap != null) {
                log.info("create initConfigMap for {}", String.valueOf(initConfigMap.getMetadata().getName()));
                coreV1Api.createNamespacedConfigMap(namespace, initConfigMap, null, null, null, null);
            }
        } catch (ApiException | NullPointerException e) {
            throw new K8sFrameworkException(e.getMessage());
        }

        log.info("create job for {}", String.valueOf(job.getMetadata().getName()));
        job = create(job);

        //update state
        runnable.setState(State.RUNNING.name());

        //update results
        try {
            runnable.setResults(Map.of("cronJob", mapper.convertValue(job, typeRef)));
        } catch (IllegalArgumentException e) {
            log.error("error reading k8s results: {}", e.getMessage());
        }

        if (log.isTraceEnabled()) {
            log.trace("result: {}", runnable);
        }

        return runnable;
    }

    @Override
    public K8sCronJobRunnable stop(K8sCronJobRunnable runnable) throws K8sFrameworkException {
        log.info("stop for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        V1CronJob job = get(build(runnable));

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
    public K8sCronJobRunnable delete(K8sCronJobRunnable runnable) throws K8sFrameworkException {
        log.info("delete for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        V1CronJob job;
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
    public V1CronJob build(K8sCronJobRunnable runnable) throws K8sFrameworkException {
        log.debug("build for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }
        // Generate jobName and ContainerName
        String jobName = k8sBuilderHelper.getJobName(runnable.getRuntime(), runnable.getTask(), runnable.getId());
        log.debug("build k8s job for {}", jobName);

        //build labels
        Map<String, String> labels = buildLabels(runnable);

        // Create the Job metadata
        V1ObjectMeta metadata = new V1ObjectMeta().name(jobName).labels(labels);

        if (!StringUtils.hasText(runnable.getSchedule())) {
            throw new K8sFrameworkException("missing or invalid schedule in spec");
        }

        K8sJobRunnable k8sJobRunnable = K8sJobRunnable
            .builder()
            .id(runnable.getId())
            .args(runnable.getArgs())
            .affinity(runnable.getAffinity())
            .backoffLimit(runnable.getBackoffLimit())
            .command(runnable.getCommand())
            .envs(runnable.getEnvs())
            .image(runnable.getImage())
            .labels(runnable.getLabels())
            .nodeSelector(runnable.getNodeSelector())
            .project(runnable.getProject())
            .resources(runnable.getResources())
            .runtime(runnable.getRuntime())
            .secrets(runnable.getSecrets())
            .task(runnable.getTask())
            .tolerations(runnable.getTolerations())
            .runtimeClass(runnable.getRuntimeClass())
            .priorityClass(runnable.getPriorityClass())
            .volumes(runnable.getVolumes())
            .state(State.READY.name())
            .build();

        V1Job job = jobFramework.build(k8sJobRunnable);
        V1CronJobSpec cronJobSpec = new V1CronJobSpec()
            .schedule(runnable.getSchedule())
            .jobTemplate(new V1JobTemplateSpec().spec(job.getSpec()));

        return new V1CronJob().metadata(metadata).spec(cronJobSpec);
    }

    /*
     * K8s
     */
    @Override
    public V1CronJob apply(@NotNull V1CronJob job) throws K8sFrameworkException {
        //nothing to do
        return job;
    }

    @Override
    public V1CronJob get(@NotNull V1CronJob job) throws K8sFrameworkException {
        Assert.notNull(job.getMetadata(), "metadata can not be null");

        try {
            String jobName = job.getMetadata().getName();
            log.debug("get k8s job for {}", jobName);

            return batchV1Api.readNamespacedCronJob(jobName, namespace, null);
        } catch (ApiException e) {
            log.info("Error with k8s: {}", e.getMessage());
            if (log.isTraceEnabled()) {
                log.trace("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getMessage());
        }
    }

    @Override
    public V1CronJob create(V1CronJob job) throws K8sFrameworkException {
        Assert.notNull(job.getMetadata(), "metadata can not be null");

        try {
            String jobName = job.getMetadata().getName();
            log.debug("create k8s job for {}", jobName);

            //dispatch job via api
            return batchV1Api.createNamespacedCronJob(namespace, job, null, null, null, null);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getResponseBody());
            if (log.isTraceEnabled()) {
                log.trace("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getResponseBody());
        }
    }

    @Override
    public void delete(V1CronJob job) throws K8sFrameworkException {
        Assert.notNull(job.getMetadata(), "metadata can not be null");

        try {
            String jobName = job.getMetadata().getName();
            log.debug("delete k8s job for {}", jobName);

            batchV1Api.deleteNamespacedCronJob(jobName, namespace, null, null, null, null, null, null);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getResponseBody());
            if (log.isTraceEnabled()) {
                log.trace("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getResponseBody());
        }
    }
}
