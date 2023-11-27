package it.smartcommunitylabdhub.modules.nefertem.components.runners;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.Runnable;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecEntity;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunAccessor;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunUtils;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.core.utils.BeanProvider;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.function.FunctionNefertemSpec;

import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * DbtMetricRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "dbt", task = "metric")
 */
public class NefertemMetricRunner implements Runner {

    private String image;

    public NefertemMetricRunner(String image) {
        this.image = image;
    }

    @Override
    public Runnable produce(Run runDTO) {

        return Optional.ofNullable(runDTO)
                .map(this::validateRunDTO)
                .orElseThrow(() -> new IllegalArgumentException("Invalid runDTO"));

    }

    private K8sJobRunnable validateRunDTO(Run runDTO) {

        SpecRegistry<? extends Spec> specRegistry =
                BeanProvider.getSpecRegistryBean(SpecRegistry.class)
                        .orElseThrow(() -> new RuntimeException("SpecRegistry not found"));


        // Retrieve run spec from registry
        RunRunSpec runRunSpec = specRegistry.createSpec(
                runDTO.getKind(),
                SpecEntity.RUN,
                runDTO.getSpec()
        );
        // Create accessor for run
        RunAccessor runAccessor = RunUtils.parseRun(runRunSpec.getTask());

        // Retrieve function spec from registry
        FunctionNefertemSpec functionNefertemSpec = specRegistry.createSpec(
                runAccessor.getRuntime(),
                SpecEntity.FUNCTION,
                runDTO.getSpec()
        );


        if (functionNefertemSpec.getExtraSpecs() == null) {
            throw new IllegalArgumentException(
                    "Invalid argument: args not found in runDTO spec");
        }

        K8sJobRunnable k8sJobRunnable = K8sJobRunnable.builder()
                .runtime(runAccessor.getRuntime())
                .task(runAccessor.getTask())
                .image(image)
                .command("python")
                .args(List.of("wrapper.py").toArray(String[]::new))
                .envs(Map.of(
                        "PROJECT_NAME", runDTO.getProject(),
                        "RUN_ID", runDTO.getId()))
                .state(runDTO.getState())
                .build();

        k8sJobRunnable.setId(runDTO.getId());
        k8sJobRunnable.setProject(runDTO.getProject());

        return k8sJobRunnable;

    }
}
