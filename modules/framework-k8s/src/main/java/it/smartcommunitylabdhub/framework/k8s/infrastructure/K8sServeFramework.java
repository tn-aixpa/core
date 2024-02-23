package it.smartcommunitylabdhub.framework.k8s.infrastructure;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.*;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.FrameworkComponent;
import it.smartcommunitylabdhub.commons.services.LogService;
import it.smartcommunitylabdhub.commons.services.RunService;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sServeRunnable;
import it.smartcommunitylabdhub.fsm.pollers.PollingService;
import it.smartcommunitylabdhub.fsm.types.RunStateMachine;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

//TODO: le operazioni di clean del deployment vanno fatte nel framework
@Slf4j
@FrameworkComponent(framework = "k8sserve")
public class K8sServeFramework extends K8sBaseFramework<K8sServeRunnable, V1Service> {

    private final K8sDeploymentFramework deploymentFramework;

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

    public K8sServeFramework(ApiClient apiClient) {
        super(apiClient);
        deploymentFramework = new K8sDeploymentFramework(apiClient);
    }

    // TODO: instead of void define a Result object that have to be merged with the run from the
    // caller.
    @Override
    public void execute(K8sServeRunnable runnable) throws K8sFrameworkException {
        V1Service service = apply(runnable);
        //TODO monitor
    }

    public V1Service apply(K8sServeRunnable runnable) throws K8sFrameworkException {
        try {
            // Log service execution initiation
            log.info("----------------- PREPARE KUBERNETES Serve ----------------");

            // Generate deploymentName and ContainerName
            String serviceName = k8sBuilderHelper.getServiceName(
                runnable.getRuntime(),
                runnable.getTask(),
                runnable.getId()
            );
            V1Deployment deployment = deploymentFramework.apply(runnable);
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
                .map(p -> new V1ServicePort().port(p.port()).targetPort(new IntOrString(p.targetPort())).protocol("TCP")
                )
                .collect(Collectors.toList());

            // service type (ClusterIP or NodePort)
            String type = Optional.ofNullable(runnable.getServiceType()).orElse("NodePort");

            //build service spec
            V1ServiceSpec serviceSpec = new V1ServiceSpec().type(type).ports(ports).selector(labels);
            V1ObjectMeta serviceMetadata = new V1ObjectMeta().name(serviceName);
            V1Service service = new V1Service().metadata(serviceMetadata).spec(serviceSpec);

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
}
