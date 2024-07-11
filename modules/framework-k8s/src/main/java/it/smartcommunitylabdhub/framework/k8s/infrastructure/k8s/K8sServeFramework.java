package it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s;

import com.fasterxml.jackson.core.type.TypeReference;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.FrameworkComponent;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sDeploymentRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sServeRunnable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

@Slf4j
@FrameworkComponent(framework = K8sServeFramework.FRAMEWORK)
public class K8sServeFramework extends K8sBaseFramework<K8sServeRunnable, V1Service> {

    public static final String FRAMEWORK = "k8sserve";
    private static final TypeReference<HashMap<String, Serializable>> typeRef = new TypeReference<
        HashMap<String, Serializable>
    >() {};

    @Value("${kaniko.init-image}")
    private String initImage;

    @Autowired
    private K8sDeploymentFramework deploymentFramework;

    public K8sServeFramework(ApiClient apiClient) {
        super(apiClient);
    }

    @Override
    public K8sServeRunnable run(K8sServeRunnable runnable) throws K8sFrameworkException {
        log.info("run for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        // Create a deployment from a Deployment+Service
        V1Deployment deployment = buildDeployment(runnable);

        try {
            V1ConfigMap initConfigMap = buildInitConfigMap(runnable);
            if (initConfigMap != null) {
                log.info("create initConfigMap for {}", String.valueOf(initConfigMap.getMetadata().getName()));
                coreV1Api.createNamespacedConfigMap(namespace, initConfigMap, null, null, null, null);
            }
        } catch (ApiException | NullPointerException e) {
            throw new K8sFrameworkException(e.getMessage());
        }

        log.info("create deployment for {}", String.valueOf(deployment.getMetadata().getName()));
        deployment = deploymentFramework.create(deployment);

        //create the service
        V1Service service = build(runnable);
        log.info("create service for {}", String.valueOf(service.getMetadata().getName()));

        service = create(service);

        //update state
        runnable.setState(State.RUNNING.name());

        //update results
        try {
            runnable.setResults(
                Map.of(
                    "deployment",
                    mapper.convertValue(deployment, typeRef),
                    "service",
                    mapper.convertValue(service, typeRef)
                )
            );
        } catch (IllegalArgumentException e) {
            log.error("error reading k8s results: {}", e.getMessage());
        }

        if (log.isTraceEnabled()) {
            log.trace("result: {}", runnable);
        }

        return runnable;
    }

    @Override
    public K8sServeRunnable delete(K8sServeRunnable runnable) throws K8sFrameworkException {
        log.info("delete for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        V1Deployment deployment;
        try {
            // Retrieve the deployment
            deployment = deploymentFramework.get(buildDeployment(runnable));
        } catch (K8sFrameworkException e) {
            deployment = null;
        }

        if (deployment != null) {
            // Delete the deployment
            log.info("delete deployment for {}", String.valueOf(deployment.getMetadata().getName()));

            deploymentFramework.delete(deployment);
        }

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

        V1Service service;
        try {
            // Retrieve the service
            service = get(build(runnable));
        } catch (K8sFrameworkException e) {
            runnable.setState(State.DELETED.name());
            return runnable;
        }

        //Delete the service
        log.info("delete service for {}", String.valueOf(service.getMetadata().getName()));
        delete(service);

        //update results
        try {
            runnable.setResults(
                Map.of(
                    "deployment",
                    deployment != null ? mapper.convertValue(deployment, typeRef) : null,
                    "service",
                    mapper.convertValue(service, typeRef)
                )
            );
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
    public K8sServeRunnable stop(K8sServeRunnable runnable) throws K8sFrameworkException {
        log.info("stop for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        //stop deployment and delete service
        V1Deployment deployment = deploymentFramework.get(buildDeployment(runnable));
        if (deployment != null) {
            log.info("stop deployment for {}", String.valueOf(deployment.getMetadata().getName()));
            //stop by setting replicas to 0
            deployment.getSpec().setReplicas(0);
            deploymentFramework.apply(deployment);
        }

        V1Service service = get(build(runnable));
        log.info("delete service for {}", String.valueOf(service.getMetadata().getName()));
        delete(service);

        //update results
        try {
            runnable.setResults(
                Map.of(
                    "deployment",
                    deployment != null ? mapper.convertValue(deployment, typeRef) : null,
                    "service",
                    service != null ? mapper.convertValue(service, typeRef) : null
                )
            );
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
    public V1Service build(K8sServeRunnable runnable) throws K8sFrameworkException {
        log.debug("build for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        // Generate deploymentName and ContainerName
        String serviceName = k8sBuilderHelper.getServiceName(
            runnable.getRuntime(),
            runnable.getTask(),
            runnable.getId()
        );

        log.debug("build k8s service for {}", serviceName);

        Map<String, String> labels = buildLabels(runnable);
        // Create the V1 service
        // TODO: the service definition contains a list of ports. service: { ports:[xxx,xxx,,xxx],.....}

        if (runnable.getServicePorts() == null || runnable.getServicePorts().isEmpty()) {
            log.warn("no service ports specified for {}", serviceName);
        }

        //build ports
        List<V1ServicePort> ports = Optional
            .ofNullable(runnable.getServicePorts())
            .map(list ->
                list
                    .stream()
                    .filter(p -> p.port() != null && p.targetPort() != null)
                    .map(p ->
                        new V1ServicePort().port(p.port()).targetPort(new IntOrString(p.targetPort())).protocol("TCP")
                    )
                    .collect(Collectors.toList())
            )
            .orElse(null);

        // service type (ClusterIP or NodePort)
        String type = Optional.ofNullable(runnable.getServiceType().name()).orElse("NodePort");

        //build service spec
        V1ServiceSpec serviceSpec = new V1ServiceSpec().type(type).ports(ports).selector(labels);
        V1ObjectMeta serviceMetadata = new V1ObjectMeta().name(serviceName).labels(labels);

        return new V1Service().metadata(serviceMetadata).spec(serviceSpec);
    }

    /*
     * K8s
     */
    @Override
    public V1Service apply(@NotNull V1Service service) throws K8sFrameworkException {
        Assert.notNull(service.getMetadata(), "metadata can not be null");

        try {
            String serviceName = service.getMetadata().getName();
            log.debug("update k8s service for {}", serviceName);

            return coreV1Api.replaceNamespacedService(serviceName, namespace, service, null, null, null, null);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getMessage());
        }
    }

    @Override
    public V1Service get(@NotNull V1Service service) throws K8sFrameworkException {
        Assert.notNull(service.getMetadata(), "metadata can not be null");

        try {
            String serviceName = service.getMetadata().getName();
            log.debug("get k8s service for {}", serviceName);

            return coreV1Api.readNamespacedService(serviceName, namespace, null);
        } catch (ApiException e) {
            log.info("Error with k8s: {}", e.getResponseBody());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getResponseBody());
        }
    }

    @Override
    public V1Service create(V1Service service) throws K8sFrameworkException {
        Assert.notNull(service.getMetadata(), "metadata can not be null");

        try {
            String serviceName = service.getMetadata().getName();
            log.debug("create k8s service for {}", serviceName);

            return coreV1Api.createNamespacedService(namespace, service, null, null, null, null);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getMessage());
        }
    }

    @Override
    public void delete(V1Service service) throws K8sFrameworkException {
        // Delete also the Service
        try {
            Assert.notNull(service.getMetadata(), "metadata can not be null");

            String serviceName = service.getMetadata().getName();
            log.debug("delete k8s service for {}", serviceName);

            coreV1Api.deleteNamespacedService(serviceName, namespace, null, null, null, null, null, null);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getMessage());
        }
    }

    /**
     * A method to build a V1Deployment using the provided K8sServeRunnable.
     *
     * @param runnable the K8sServeRunnable to build the deployment from
     * @return the built V1Deployment
     */
    public V1Deployment buildDeployment(K8sServeRunnable runnable) throws K8sFrameworkException {
        K8sDeploymentRunnable k8sDeploymentRunnable = getDeployment(runnable);
        return deploymentFramework.build(k8sDeploymentRunnable);
    }

    /**
     * Retrieves a K8sDeploymentRunnable based on the provided K8sServeRunnable.
     *
     * @param runnable The K8sServeRunnable to be used for creating the K8sDeploymentRunnable
     * @return The created K8sDeploymentRunnable
     */
    private K8sDeploymentRunnable getDeployment(K8sServeRunnable runnable) {
        return K8sDeploymentRunnable
            .builder()
            .id(runnable.getId())
            .args(runnable.getArgs())
            .image(runnable.getImage())
            .command(runnable.getCommand())
            .affinity(runnable.getAffinity())
            .labels(runnable.getLabels())
            .envs(runnable.getEnvs())
            .nodeSelector(runnable.getNodeSelector())
            .replicas(runnable.getReplicas())
            .resources(runnable.getResources())
            .project(runnable.getProject())
            .runtime(runnable.getRuntime())
            .secrets(runnable.getSecrets())
            .task(runnable.getTask())
            .state(runnable.getState())
            .tolerations(runnable.getTolerations())
            .runtimeClass(runnable.getRuntimeClass())
            .priorityClass(runnable.getPriorityClass())
            .volumes(runnable.getVolumes())
            .contextRefs(runnable.getContextRefs())
            .contextSources(runnable.getContextSources())
            .build();
    }
}
