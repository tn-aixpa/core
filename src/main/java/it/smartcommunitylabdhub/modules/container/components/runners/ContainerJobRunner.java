package it.smartcommunitylabdhub.modules.container.components.runners;


import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.Runnable;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.core.components.infrastructure.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunAccessor;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.utils.MapUtils;
import it.smartcommunitylabdhub.modules.container.models.specs.function.FunctionContainerSpec;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * ContainerDeployRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "container", task = "job")
 */
public class ContainerJobRunner implements Runner {

    private final String image;
    private final RunDefaultFieldAccessor runDefaultFieldAccessor;
    private final FunctionContainerSpec functionContainerSpec;
    private final RunAccessor runAccessor;

    public ContainerJobRunner(String image,
                              FunctionContainerSpec functionContainerSpec,
                              RunDefaultFieldAccessor runDefaultFieldAccessor,
                              RunAccessor runAccessor) {
        this.image = image;
        this.functionContainerSpec = functionContainerSpec;
        this.runDefaultFieldAccessor = runDefaultFieldAccessor;
        this.runAccessor = runAccessor;
    }

    @Override
    public Runnable produce(Run runDTO) {
        return Optional.of(runDTO)
                .map(r -> this.validateJobRunDTO(r, runAccessor))
                .orElseThrow(() -> new IllegalArgumentException("Invalid runDTO"));
    }

    /**
     * Return a runnable of type K8sJobRunnable
     *
     * @param runDTO
     * @param runAccessor
     * @return K8sJobRunnable
     */
    private K8sJobRunnable validateJobRunDTO(Run runDTO, RunAccessor runAccessor) {


        K8sJobRunnable k8sJobRunnable = K8sJobRunnable.builder()
                .runtime(runAccessor.getRuntime())
                .task(runAccessor.getTask())
                .image(image)
                .state(runDefaultFieldAccessor.getState())
                .envs(MapUtils.mergeMultipleMaps(Map.of(
                                "PROJECT_NAME", runDTO.getProject(),
                                "RUN_ID", runDTO.getId()),
                        Optional.ofNullable(functionContainerSpec.getEnvs()).orElse(Map.of())))
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
