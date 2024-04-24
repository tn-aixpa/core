package it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

//TODO: le operazioni di clean del deployment vanno fatte nel framework
// TODO: instead of void define a Result object that have to be merged with the run from the
// caller.
@Slf4j
@FrameworkComponent(framework = K8sServeFramework.FRAMEWORK)
public class K8sServeFramework extends K8sBaseFramework<K8sServeRunnable, V1Service> {

    public static final String FRAMEWORK = "k8sserve";

    @Autowired
    private K8sDeploymentFramework deploymentFramework;

    public K8sServeFramework(ApiClient apiClient) {
        super(apiClient);
    }

    @Override
    public K8sServeRunnable run(K8sServeRunnable runnable) throws K8sFrameworkException {
        // Create a deployment from a Deployment+Service
        V1Deployment deployment = buildDeployment(runnable);
        deployment = deploymentFramework.create(deployment);

        //create the service
        V1Service service = build(runnable);
        service = create(service);
        runnable.setState(State.RUNNING.name());

        return runnable;
    }

    @Override
    public K8sServeRunnable delete(K8sServeRunnable runnable) throws K8sFrameworkException {
        V1Deployment deployment;
        try {
            // Retrieve the deployment
            deployment = deploymentFramework.get(buildDeployment(runnable));
        } catch (K8sFrameworkException e) {
            deployment = null;
        }

        if (deployment != null) {
            // Delete the deployment
            deploymentFramework.delete(deployment);
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
        delete(service);

        //update state
        runnable.setState(State.DELETED.name());

        return runnable;
    }

    @Override
    public K8sServeRunnable stop(K8sServeRunnable runnable) throws K8sFrameworkException {
        //stop deployment and delete service
        deploymentFramework.stop(getDeployment(runnable));

        V1Service service = get(build(runnable));
        delete(service);

        runnable.setState(State.STOPPED.name());

        return runnable;
    }

    @Override
    public V1Service build(K8sServeRunnable runnable) throws K8sFrameworkException {
        // Log service execution initiation
        log.info("----------------- PREPARE KUBERNETES Serve ----------------");

        // Generate deploymentName and ContainerName
        String serviceName = k8sBuilderHelper.getServiceName(
            runnable.getRuntime(),
            runnable.getTask(),
            runnable.getId()
        );
        Map<String, String> labels = buildLabels(runnable);
        // Create the V1 service
        // TODO: the service definition contains a list of ports. service: { ports:[xxx,xxx,,xxx],.....}

        if (runnable.getServicePorts() == null || runnable.getServicePorts().isEmpty()) {
            log.warn("no service ports specified for {}", serviceName);
        }

        //build ports
        List<V1ServicePort> ports = runnable
            .getServicePorts()
            .stream()
            .filter(p -> p.port() != null && p.targetPort() != null)
            .map(p -> new V1ServicePort().port(p.port()).targetPort(new IntOrString(p.targetPort())).protocol("TCP"))
            .collect(Collectors.toList());

        // service type (ClusterIP or NodePort)
        String type = Optional.of(runnable.getServiceType().name()).orElse("NodePort");

        //build service spec
        V1ServiceSpec serviceSpec = new V1ServiceSpec().type(type).ports(ports).selector(labels);
        V1ObjectMeta serviceMetadata = new V1ObjectMeta().name(serviceName);

        return new V1Service().metadata(serviceMetadata).spec(serviceSpec);
    }

    @Override
    public V1Service apply(@NotNull V1Service service) throws K8sFrameworkException {
        //nothing to do
        return service;
    }

    @Override
    public V1Service get(@NotNull V1Service service) throws K8sFrameworkException {
        Assert.notNull(service.getMetadata(), "metadata can not be null");

        try {
            // Log service execution initiation
            log.info("----------------- GET KUBERNETES Serve ----------------");

            return coreV1Api.readNamespacedService(service.getMetadata().getName(), namespace, null);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getResponseBody());
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
            // Log service execution initiation
            log.info("----------------- APPLY KUBERNETES Serve ----------------");

            V1Service createdService = coreV1Api.createNamespacedService(namespace, service, null, null, null, null);
            log.info("Serve created: " + Objects.requireNonNull(createdService.getMetadata()).getName());

            return createdService;
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

            // Log service execution initiation
            log.info("----------------- DELETE KUBERNETES Serve ----------------");

            coreV1Api.deleteNamespacedService(
                service.getMetadata().getName(),
                namespace,
                null,
                null,
                null,
                null,
                null,
                null
            );
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
            .volumes(runnable.getVolumes())
            .build();
    }
}
