package it.smartcommunitylabdhub.runtime.container.runners;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.infrastructure.Runner;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sServeRunnable;
import it.smartcommunitylabdhub.runtime.container.ContainerRuntime;
import it.smartcommunitylabdhub.runtime.container.models.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.runtime.container.models.specs.run.RunContainerSpec;
import java.util.*;

/**
 * ContainerDeployRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "container", task = "deploy")
 */
public class ContainerServeRunner implements Runner {

    private static final String TASK = "serve";
    private final FunctionContainerSpec functionContainerSpec;
    private final Map<String, Set<String>> groupedSecrets;

    public ContainerServeRunner(FunctionContainerSpec functionContainerSpec, Map<String, Set<String>> groupedSecrets) {
        this.functionContainerSpec = functionContainerSpec;
        this.groupedSecrets = groupedSecrets;
    }

    @Override
    public K8sServeRunnable produce(Run runDTO) {
        RunContainerSpec runContainerSpec = RunContainerSpec.builder().build();
        runContainerSpec.configure(runDTO.getSpec());

        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(runDTO.getStatus());

        List<CoreEnv> coreEnvList = new ArrayList<>(
            List.of(new CoreEnv("PROJECT_NAME", runDTO.getProject()), new CoreEnv("RUN_ID", runDTO.getId()))
        );

        Optional.ofNullable(runContainerSpec.getTaskServeSpec().getEnvs()).ifPresent(coreEnvList::addAll);

        K8sServeRunnable k8sServeRunnable = K8sServeRunnable
            .builder()
            .runtime(ContainerRuntime.RUNTIME)
            .task(TASK)
            .image(functionContainerSpec.getImage())
            .state(statusFieldAccessor.getState())
            .resources(runContainerSpec.getTaskServeSpec().getResources())
            .nodeSelector(runContainerSpec.getTaskServeSpec().getNodeSelector())
            .volumes(runContainerSpec.getTaskServeSpec().getVolumes())
            .secrets(groupedSecrets)
            .envs(coreEnvList)
            .labels(runContainerSpec.getTaskServeSpec().getLabels())
            .affinity(runContainerSpec.getTaskServeSpec().getAffinity())
            .tolerations(runContainerSpec.getTaskServeSpec().getTolerations())
            .build();

        Optional
            .ofNullable(functionContainerSpec.getArgs())
            .ifPresent(args ->
                k8sServeRunnable.setArgs(
                    args.stream().filter(Objects::nonNull).map(Object::toString).toArray(String[]::new)
                )
            );

        Optional.ofNullable(functionContainerSpec.getEntrypoint()).ifPresent(k8sServeRunnable::setEntrypoint);

        k8sServeRunnable.setId(runDTO.getId());
        k8sServeRunnable.setProject(runDTO.getProject());

        return k8sServeRunnable;
    }
}
