package it.smartcommunitylabdhub.modules.container.components.runners;


import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.core.components.infrastructure.objects.CoreEnv;
import it.smartcommunitylabdhub.core.components.infrastructure.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.modules.container.components.runtimes.ContainerRuntime;
import it.smartcommunitylabdhub.modules.container.models.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.run.RunContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskJobSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * ContainerJobRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "container", task = "job")
 */
public class ContainerJobRunner implements Runner {

    private final static String TASK = "job";
    private final String image;
    private final RunDefaultFieldAccessor runDefaultFieldAccessor;
    private final FunctionContainerSpec functionContainerSpec;

    public ContainerJobRunner(String image,
                              FunctionContainerSpec functionContainerSpec,
                              RunDefaultFieldAccessor runDefaultFieldAccessor) {
        this.image = image;
        this.functionContainerSpec = functionContainerSpec;
        this.runDefaultFieldAccessor = runDefaultFieldAccessor;
    }

    @Override
    public K8sJobRunnable produce(Run runDTO) {

        RunContainerSpec<TaskJobSpec> runContainerSpec = RunContainerSpec.<TaskJobSpec>builder().build();
        runContainerSpec.configure(runDTO.getSpec());


        List<CoreEnv> coreEnvList = new ArrayList<>(List.of(
                new CoreEnv("PROJECT_NAME", runDTO.getProject()),
                new CoreEnv("RUN_ID", runDTO.getId())
        ));

        if (runContainerSpec.getK8sTaskBaseSpec().getEnvs() != null)
                coreEnvList.addAll(runContainerSpec.getK8sTaskBaseSpec().getEnvs());

        K8sJobRunnable k8sJobRunnable = K8sJobRunnable.builder()
                .runtime(ContainerRuntime.RUNTIME)
                .task(TASK)
                .image(image)
                .state(runDefaultFieldAccessor.getState())
                .envs(coreEnvList)
                .build();

        Optional.ofNullable(functionContainerSpec.getArgs())
                .ifPresent(args -> k8sJobRunnable.setArgs(
                                args.stream()
                                        .filter(Objects::nonNull)
                                        .map(Object::toString)
                                        .toArray(String[]::new)
                        )
                );

        Optional.ofNullable(functionContainerSpec.getCommand())
                .ifPresent(k8sJobRunnable::setCommand);

        k8sJobRunnable.setId(runDTO.getId());
        k8sJobRunnable.setProject(runDTO.getProject());

        return k8sJobRunnable;
    }

}
