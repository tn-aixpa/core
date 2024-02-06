package it.smartcommunitylabdhub.modules.container.components.runners;


import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.core.components.infrastructure.objects.CoreEnv;
import it.smartcommunitylabdhub.core.components.infrastructure.runnables.K8sDeploymentRunnable;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.modules.container.components.runtimes.ContainerRuntime;
import it.smartcommunitylabdhub.modules.container.models.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.run.RunContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskDeploySpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * ContainerDeployRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "container", task = "deploy")
 */
public class ContainerDeployRunner implements Runner {

    private final static String TASK = "deploy";

    private final String image;
    private final RunDefaultFieldAccessor runDefaultFieldAccessor;
    private final FunctionContainerSpec functionContainerSpec;

    public ContainerDeployRunner(String image,
                                 FunctionContainerSpec functionContainerSpec,
                                 RunDefaultFieldAccessor runDefaultFieldAccessor) {
        this.image = image;
        this.functionContainerSpec = functionContainerSpec;
        this.runDefaultFieldAccessor = runDefaultFieldAccessor;
    }

    @Override
    public K8sDeploymentRunnable produce(Run runDTO) {

        // Retrieve information about RunDbtSpec

        RunContainerSpec<TaskDeploySpec> runContainerSpec = RunContainerSpec.<TaskDeploySpec>builder().build();
        runContainerSpec.configure(runDTO.getSpec());


        List<CoreEnv> coreEnvList = new ArrayList<>(List.of(
                new CoreEnv("PROJECT_NAME", runDTO.getProject()),
                new CoreEnv("RUN_ID", runDTO.getId())
        ));

        if (runContainerSpec.getK8sTaskBaseSpec().getEnvs() != null)
                coreEnvList.addAll(runContainerSpec.getK8sTaskBaseSpec().getEnvs());


        K8sDeploymentRunnable k8sDeploymentRunnable = K8sDeploymentRunnable.builder()
                .runtime(ContainerRuntime.RUNTIME) //TODO: delete accessor.
                .task(TASK)
                .image(image)
                .state(runDefaultFieldAccessor.getState())
                .resources(runContainerSpec.getK8sTaskBaseSpec().getResources())
                .nodeSelector(runContainerSpec.getK8sTaskBaseSpec().getNodeSelector())
                .volumes(runContainerSpec.getK8sTaskBaseSpec().getVolumes())
                //.secrets(runDbtSpec.getTaskTransformSpec().getSecrets())
                .envs(coreEnvList)
                .build();

        Optional.ofNullable(functionContainerSpec.getArgs())
                .ifPresent(args -> k8sDeploymentRunnable.setArgs(
                                args.stream()
                                        .filter(Objects::nonNull)
                                        .map(Object::toString)
                                        .toArray(String[]::new)
                        )
                );

        Optional.ofNullable(functionContainerSpec.getEntrypoint())
                .ifPresent(k8sDeploymentRunnable::setEntrypoint);

        k8sDeploymentRunnable.setId(runDTO.getId());
        k8sDeploymentRunnable.setProject(runDTO.getProject());

        return k8sDeploymentRunnable;

    }
}
