package it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.*;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.FrameworkComponent;
import it.smartcommunitylabdhub.commons.models.entities.run.RunState;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.LogService;
import it.smartcommunitylabdhub.commons.services.RunService;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sDeploymentRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sServeRunnable;
import it.smartcommunitylabdhub.fsm.pollers.PollingService;
import it.smartcommunitylabdhub.fsm.types.RunStateMachine;
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
@Slf4j
@FrameworkComponent(framework = K8sServeFramework.FRAMEWORK)
public class K8sServeFramework extends K8sBaseFramework<K8sServeRunnable, V1Service> {

    public static final String FRAMEWORK = "k8sserve";
    //TODO drop
    @Autowired
    PollingService pollingService;
    //TODO drop from framework, this should be delegated to run listener/service
    //the framework has NO concept of runs, only RUNNABLEs
    @Autowired
    RunStateMachine runStateMachine;
    //TODO drop, logs must be handled by a listener
    @Autowired
    LogService logService;
    //TODO drop from framework, this should be delegated to run listener/service
    //the framework has NO concept of runs, only RUNNABLEs
    @Autowired
    RunService runService;
    @Autowired
    private K8sDeploymentFramework deploymentFramework;


    public K8sServeFramework(ApiClient apiClient) {
        super(apiClient);
    }

    // TODO: instead of void define a Result object that have to be merged with the run from the
    // caller.
    @Override
    public K8sServeRunnable execute(K8sServeRunnable runnable) throws K8sFrameworkException {

        V1Deployment deployment = buildDeployment(runnable);
        deployment = deploymentFramework.apply(deployment);

        V1Service service = build(runnable);
        service = apply(service);

        runnable.setState(State.RUNNING.name());
        //TODO monitor

        return runnable;
    }

    public V1Deployment buildDeployment(K8sServeRunnable runnable) throws K8sFrameworkException {
        K8sDeploymentRunnable k8sDeploymentRunnable = K8sDeploymentRunnable.builder()
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
                .state(RunState.READY.name())
                .tolerations(runnable.getTolerations())
                .volumes(runnable.getVolumes())
                .build();

        return deploymentFramework.build(k8sDeploymentRunnable);
    }

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
        List<V1ServicePort> ports = Optional
                .ofNullable(runnable.getServicePorts())
                .orElse(null)
                .stream()
                .filter(p -> p.port() != null && p.targetPort() != null)
                .map(p -> new V1ServicePort()
                        .port(p.port())
                        .targetPort(new IntOrString(p.targetPort())).protocol("TCP"))
                .collect(Collectors.toList());

        // service type (ClusterIP or NodePort)
        String type = Optional.ofNullable(runnable.getServiceType().name()).orElse("NodePort");

        //build service spec
        V1ServiceSpec serviceSpec = new V1ServiceSpec().type(type).ports(ports).selector(labels);
        V1ObjectMeta serviceMetadata = new V1ObjectMeta().name(serviceName);

        return new V1Service().metadata(serviceMetadata).spec(serviceSpec);
    }

    public V1Service apply(@NotNull V1Service service) throws K8sFrameworkException {
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
}
