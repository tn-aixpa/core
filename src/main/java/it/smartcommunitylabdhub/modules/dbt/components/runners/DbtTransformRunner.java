package it.smartcommunitylabdhub.modules.dbt.components.runners;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.core.components.infrastructure.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.modules.dbt.components.runtimes.DbtRuntime;
import it.smartcommunitylabdhub.modules.dbt.models.specs.run.RunDbtSpec;

import java.util.List;
import java.util.Map;


/**
 * DbtTransformRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "dbt", task = "transform")
 */
public class DbtTransformRunner implements Runner {
    private static final String TASK = "transform";

    private final String image;
    private final RunDefaultFieldAccessor runDefaultFieldAccessor;

    public DbtTransformRunner(String image,
                              RunDefaultFieldAccessor runDefaultFieldAccessor) {
        this.image = image;
        this.runDefaultFieldAccessor = runDefaultFieldAccessor;
    }

    @Override
    public K8sJobRunnable produce(Run runDTO) {
        
        RunDbtSpec runDbtSpec = RunDbtSpec.builder().build();
        runDbtSpec.configure(runDTO.getSpec());

        K8sJobRunnable k8sJobRunnable = K8sJobRunnable.builder()
                .runtime(DbtRuntime.RUNTIME)
                .task("transform")
                .image(image)
                .command("python")
                .args(List.of("wrapper.py").toArray(String[]::new))
                .envs(Map.of(
                        "PROJECT_NAME", runDTO.getProject(),
                        "RUN_ID", runDTO.getId()))
                .state(runDefaultFieldAccessor.getState())
                .build();

        k8sJobRunnable.setId(runDTO.getId());
        k8sJobRunnable.setProject(runDTO.getProject());

        return k8sJobRunnable;
    }
}
