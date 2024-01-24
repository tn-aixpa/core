package it.smartcommunitylabdhub.modules.container.components.runners;


import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.Runnable;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunAccessor;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunUtils;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.core.utils.BeanProvider;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
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

    private String image;
    private SpecRegistry<? extends Spec> specRegistry;

    public ContainerJobRunner(String image) {

        this.image = image;
        this.specRegistry = BeanProvider.getSpecRegistryBean(SpecRegistry.class)
                .orElseThrow(() -> new RuntimeException("SpecRegistry not found"));
    }

    @Override
    public Runnable produce(Run runDTO) {
        // Retrieve run spec from registry
        RunRunSpec runRunSpec = specRegistry.createSpec(
                runDTO.getKind(),
                EntityName.RUN,
                runDTO.getSpec()
        );
        RunAccessor runAccessor = RunUtils.parseRun(runRunSpec.getTask());

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


        // Retrieve bean accessor field
        AccessorRegistry<? extends Accessor<Object>> accessorRegistry =
                BeanProvider.getAccessorRegistryBean(AccessorRegistry.class)
                        .orElseThrow(() -> new RuntimeException("AccessorRegistry not found"));


        // Retrieve accessor fields
        RunDefaultFieldAccessor runFieldAccessor =
                accessorRegistry.createAccessor(
                        runDTO.getKind(),
                        EntityName.RUN,
                        JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                                runDTO,
                                JacksonMapper.typeRef));


        // Retrieve function spec from registry
        FunctionContainerSpec functionContainerSpec = specRegistry.createSpec(
                runAccessor.getRuntime(),
                EntityName.FUNCTION,
                runDTO.getSpec()
        );


        if (functionContainerSpec.getExtraSpecs() == null) {
            throw new IllegalArgumentException(
                    "Invalid argument: args not found in runDTO spec");
        }

        String[] args = functionContainerSpec.getArgs().stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .toArray(String[]::new);

        K8sJobRunnable k8sJobRunnable = K8sJobRunnable.builder()
                .runtime(runAccessor.getRuntime())
                .task(runAccessor.getTask())
                .image(image)
                .command(functionContainerSpec.getEntrypoint())
                .args(args)
                .envs(Map.of(
                        "PROJECT_NAME", runDTO.getProject(),
                        "RUN_ID", runDTO.getId()))
                .state(runFieldAccessor.getState())
                .build();

        k8sJobRunnable.setId(runDTO.getId());
        k8sJobRunnable.setProject(runDTO.getProject());

        return k8sJobRunnable;

    }
}
