package it.smartcommunitylabdhub.modules.nefertem.components.runners;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.core.components.infrastructure.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.run.RunNefertemSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.task.TaskMetricSpec;

import java.util.List;
import java.util.Map;


/**
 * DbtMetricRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "dbt", task = "metric")
 */
public class NefertemMetricRunner implements Runner {

    private final String image;

    private final RunDefaultFieldAccessor runDefaultFieldAccessor;


    public NefertemMetricRunner(String image,
                                RunDefaultFieldAccessor runDefaultFieldAccessor) {
        this.image = image;
        this.runDefaultFieldAccessor = runDefaultFieldAccessor;
    }

    @Override
    public K8sJobRunnable produce(Run runDTO) {


        // Retrieve information about Spec
        RunNefertemSpec<TaskMetricSpec> runNefertemSpec = RunNefertemSpec.<TaskMetricSpec>builder().build();
        runNefertemSpec.configure(runDTO.getSpec());

        //TODO: Create runnable using information from Run completed spec.

        K8sJobRunnable k8sJobRunnable = K8sJobRunnable.builder()
                .runtime("nefertem")
                .task("metric")
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
