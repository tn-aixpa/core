package it.smartcommunitylabdhub.runtime.container.runners;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.infrastructure.Runner;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sServeRunnable;
import it.smartcommunitylabdhub.runtime.container.ContainerRuntime;
import it.smartcommunitylabdhub.runtime.container.specs.ContainerFunctionSpec;
import it.smartcommunitylabdhub.runtime.container.specs.ContainerRunSpec;
import it.smartcommunitylabdhub.runtime.container.specs.ContainerServeTaskSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * ContainerDeployRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "container", task = "deploy")
 */
public class ContainerServeRunner implements Runner<K8sServeRunnable> {

    private static final String TASK = "serve";

    private final ContainerFunctionSpec functionSpec;
    private final Map<String, Set<String>> groupedSecrets;

    public ContainerServeRunner(ContainerFunctionSpec functionContainerSpec, Map<String, Set<String>> groupedSecrets) {
        this.functionSpec = functionContainerSpec;
        this.groupedSecrets = groupedSecrets;
    }

    @Override
    public K8sServeRunnable produce(Run run) {
        ContainerRunSpec runSpec = new ContainerRunSpec(run.getSpec());
        ContainerServeTaskSpec taskSpec = runSpec.getTaskServeSpec();
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(run.getStatus());

        List<CoreEnv> coreEnvList = new ArrayList<>(
            List.of(new CoreEnv("PROJECT_NAME", run.getProject()), new CoreEnv("RUN_ID", run.getId()))
        );

        Optional.ofNullable(taskSpec.getEnvs()).ifPresent(coreEnvList::addAll);

        K8sServeRunnable k8sServeRunnable = K8sServeRunnable
            .builder()
            .runtime(ContainerRuntime.RUNTIME)
            .task(TASK)
            .state(State.READY.name())
            //base
            .image(functionSpec.getImage())
            .command(functionSpec.getCommand())
            .args(functionSpec.getArgs() != null ? functionSpec.getArgs().toArray(new String[0]) : null)
            .envs(coreEnvList)
            .secrets(groupedSecrets)
            .resources(taskSpec.getResources())
            .volumes(taskSpec.getVolumes())
            .nodeSelector(taskSpec.getNodeSelector())
            .affinity(taskSpec.getAffinity())
            .tolerations(taskSpec.getTolerations())
            .runtimeClass(taskSpec.getRuntimeClass())
            .priorityClass(taskSpec.getPriorityClass())
            //specific
            .servicePorts(taskSpec.getServicePorts())
            .serviceType(taskSpec.getServiceType())
            .build();

        k8sServeRunnable.setId(run.getId());
        k8sServeRunnable.setProject(run.getProject());

        return k8sServeRunnable;
    }
}
