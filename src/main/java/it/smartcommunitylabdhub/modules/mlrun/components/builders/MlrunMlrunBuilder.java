package it.smartcommunitylabdhub.modules.mlrun.components.builders;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.core.utils.MapUtils;
import it.smartcommunitylabdhub.modules.mlrun.models.specs.function.FunctionMlrunSpec;
import it.smartcommunitylabdhub.modules.mlrun.models.specs.task.TaskMlrunSpec;

import java.util.Map;

/**
 * MlrunMlrunBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "mlrun", task = "mlrun")
 */

public class MlrunMlrunBuilder implements Builder<
        FunctionMlrunSpec,
        TaskMlrunSpec,
        RunRunSpec> {

    @Override
    public RunRunSpec build(
            FunctionMlrunSpec funSpec,
            TaskMlrunSpec taskSpec,
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
