package it.smartcommunitylabdhub.modules.container.components.runners;


import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.core.components.infrastructure.objects.CoreEnv;
import it.smartcommunitylabdhub.core.components.infrastructure.runnables.K8sServeRunnable;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.modules.container.components.runtimes.ContainerRuntime;
import it.smartcommunitylabdhub.modules.container.models.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.run.RunContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskServeSpec;

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

    private final static String TASK = "serve";
    private final String image;
    private final RunDefaultFieldAccessor runDefaultFieldAccessor;
    private final FunctionContainerSpec functionContainerSpec;
    private final Map<String, Set<String>> groupedSecrets;

    public ContainerServeRunner(String image,
                                FunctionContainerSpec functionContainerSpec,
                                RunDefaultFieldAccessor runDefaultFieldAccessor, Map<String, Set<String>> groupedSecrets) {
        this.image = image;
        this.functionContainerSpec = functionContainerSpec;
        this.runDefaultFieldAccessor = runDefaultFieldAccessor;
        this.groupedSecrets = groupedSecrets;
    }

    @Override
    public K8sServeRunnable produce(Run runDTO) {

        RunContainerSpec<TaskServeSpec> runContainerSpec = RunContainerSpec.<TaskServeSpec>builder().build();
        runContainerSpec.configure(runDTO.getSpec());

        List<CoreEnv> coreEnvList = new ArrayList<>(List.of(
                new CoreEnv("PROJECT_NAME", runDTO.getProject()),
                new CoreEnv("RUN_ID", runDTO.getId())
        ));

        if (runContainerSpec.getTaskSpec().getEnvs() != null)
            coreEnvList.addAll(runContainerSpec.getTaskSpec().getEnvs());


        K8sServeRunnable k8sServeRunnable = K8sServeRunnable.builder()
                .runtime(ContainerRuntime.RUNTIME)
                .task(TASK)
                .image(image)
                .state(runDefaultFieldAccessor.getState())
                .resources(runContainerSpec.getTaskSpec().getResources())
                .nodeSelector(runContainerSpec.getTaskSpec().getNodeSelector())
                .volumes(runContainerSpec.getTaskSpec().getVolumes())
                .secrets(groupedSecrets)
                .envs(coreEnvList)
                .build();

        Optional.ofNullable(functionContainerSpec.getArgs())
                .ifPresent(args -> k8sServeRunnable.setArgs(
                                args.stream()
                                        .filter(Objects::nonNull)
                                        .map(Object::toString)
                                        .toArray(String[]::new)
                        )
                );

        Optional.ofNullable(functionContainerSpec.getEntrypoint())
                .ifPresent(k8sServeRunnable::setEntrypoint);

        k8sServeRunnable.setId(runDTO.getId());
        k8sServeRunnable.setProject(runDTO.getProject());

        return k8sServeRunnable;

    }

}
