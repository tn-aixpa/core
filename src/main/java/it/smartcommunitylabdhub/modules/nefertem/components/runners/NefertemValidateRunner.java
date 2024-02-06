package it.smartcommunitylabdhub.modules.nefertem.components.runners;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.core.components.infrastructure.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.run.RunNefertemSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.task.TaskValidateSpec;

import java.util.List;
import java.util.Map;


/**
 * DbtValidateRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "dbt", task = "validate")
 */
public class NefertemValidateRunner implements Runner {

    private final String image;
    private final RunDefaultFieldAccessor runDefaultFieldAccessor;

    public NefertemValidateRunner(String image,
                                  RunDefaultFieldAccessor runDefaultFieldAccessor) {
        this.image = image;
        this.runDefaultFieldAccessor = runDefaultFieldAccessor;
    }

    @Override
    public K8sJobRunnable produce(Run runDTO) {

        // Retrieve information spec
        RunNefertemSpec<TaskValidateSpec> runNefertemSpec = RunNefertemSpec.<TaskValidateSpec>builder().build();
        runNefertemSpec.configure(runDTO.getSpec());

        //TODO: Create runnable using information from Run completed spec.

        K8sJobRunnable k8sJobRunnable = K8sJobRunnable.builder()
                .runtime("nefertem")
                .task("validate")
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
