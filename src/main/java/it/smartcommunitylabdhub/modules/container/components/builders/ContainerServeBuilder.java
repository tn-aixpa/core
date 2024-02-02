package it.smartcommunitylabdhub.modules.container.components.builders;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.core.utils.MapUtils;
import it.smartcommunitylabdhub.modules.container.models.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskDeploySpec;

import java.util.Map;

/**
 * ContainerDeployBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "container", task = "serve")
 */

public class ContainerServeBuilder implements Builder<
        FunctionContainerSpec,
        TaskDeploySpec,
        RunRunSpec> {

    @Override
    public RunRunSpec build(
            FunctionContainerSpec funSpec,
            TaskDeploySpec taskSpec,
            RunRunSpec runSpec) {

        // Merge spec
        Map<String, Object> extraSpecs = MapUtils.mergeMultipleMaps(
                funSpec.toMap(),
                taskSpec.toMap()
        );

        // Update run specific spec
        runSpec.getExtraSpecs()
                .putAll(extraSpecs);

        // Return a run spec
        return runSpec;
    }
}

