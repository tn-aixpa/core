package it.smartcommunitylabdhub.modules.container.components.runners;


import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.Runnable;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.core.components.infrastructure.runnables.K8sDeploymentRunnable;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
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
 * @RunnerComponent(runtime = "container", task = "deploy")
 */
public class ContainerDeployRunner implements Runner {

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
    public Runnable produce(Run runDTO) {

        return Optional.of(runDTO)
                .map(this::validateDeployRunDTO)
                .orElseThrow(() -> new IllegalArgumentException("Invalid runDTO"));

    }

    /**
     * Return a K8sDeploymentRunnable
     *
     * @param runDTO
     * @return
     */
    private K8sDeploymentRunnable validateDeployRunDTO(Run runDTO) {


        K8sDeploymentRunnable k8sDeploymentRunnable = K8sDeploymentRunnable.builder()
                .runtime("container") //TODO: delete accessor.
                .task("deploy")
                .image(image)
                .state(runDefaultFieldAccessor.getState())
                .envs(MapUtils.mergeMultipleMaps(Map.of(
                                "PROJECT_NAME", runDTO.getProject(),
                                "RUN_ID", runDTO.getId()),
                        Optional.ofNullable(functionContainerSpec.getEnvs()).orElse(Map.of())))
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
