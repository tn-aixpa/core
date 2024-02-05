package it.smartcommunitylabdhub.modules.nefertem.components.runners;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.Runnable;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.core.components.infrastructure.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunAccessor;
import it.smartcommunitylabdhub.core.models.entities.run.Run;

import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * DbtProfileRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "dbt", task = "profile")
 */
public class NefertemProfileRunner implements Runner {

    private final String image;


    private final RunDefaultFieldAccessor runDefaultFieldAccessor;

    private final RunAccessor runAccessor;

    public NefertemProfileRunner(String image,
                                 RunDefaultFieldAccessor runDefaultFieldAccessor,
                                 RunAccessor runAccessor) {
        this.image = image;
        this.runDefaultFieldAccessor = runDefaultFieldAccessor;
        this.runAccessor = runAccessor;
    }

    @Override
    public Runnable produce(Run runDTO) {

        return Optional.ofNullable(runDTO)
                .map(this::validateRunDTO)
                .orElseThrow(() -> new IllegalArgumentException("Invalid runDTO"));

    }

    private K8sJobRunnable validateRunDTO(Run runDTO) {

        K8sJobRunnable k8sJobRunnable = K8sJobRunnable.builder()
                .runtime(runAccessor.getRuntime())
                .task(runAccessor.getTask())
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
