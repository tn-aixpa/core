package it.smartcommunitylabdhub.runtime.dbt;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.infrastructure.Runtime;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.services.entities.SecretService;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.runtime.dbt.builders.DbtTransformBuilder;
import it.smartcommunitylabdhub.runtime.dbt.runners.DbtTransformRunner;
import it.smartcommunitylabdhub.runtime.dbt.specs.function.FunctionDbtSpec;
import it.smartcommunitylabdhub.runtime.dbt.specs.run.RunDbtSpec;
import it.smartcommunitylabdhub.runtime.dbt.specs.task.TaskTransformSpec;
import it.smartcommunitylabdhub.runtime.dbt.status.RunDbtStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@RuntimeComponent(runtime = DbtRuntime.RUNTIME)
public class DbtRuntime implements Runtime<FunctionDbtSpec, RunDbtSpec, RunDbtStatus, K8sJobRunnable> {

    public static final String RUNTIME = "dbt";

    private final DbtTransformBuilder builder = new DbtTransformBuilder();

    @Autowired
    SecretService secretService;

    @Value("${runtime.dbt.image}")
    private String image;

    @Override
    public RunDbtSpec build(@NotNull Function function, @NotNull Task task, @NotNull Run run) {
        FunctionDbtSpec functionSpec = new FunctionDbtSpec(function.getSpec());
        RunDbtSpec runSpec = new RunDbtSpec(run.getSpec());

        String kind = task.getKind();

        // Retrieve builder using task kind
        switch (kind) {
            case TaskTransformSpec.KIND -> {
                TaskTransformSpec taskTransformSpec = new TaskTransformSpec(task.getSpec());
                return builder.build(functionSpec, taskTransformSpec, runSpec);
            }
            default -> throw new IllegalArgumentException(
                    "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task."
            );
        }
    }

    @Override
    public K8sJobRunnable run(Run run) {
        // Crete spec for run
        RunDbtSpec runSpec = new RunDbtSpec(run.getSpec());

        // Create string run accessor from task
        //TODO drop the utils and get the task accessor from the spec.
        RunSpecAccessor runAccessor = RunUtils.parseTask(runSpec.getTask());

        return switch (runAccessor.getTask()) {
            case TaskTransformSpec.KIND -> new DbtTransformRunner(
                    image,
                    secretService.groupSecrets(run.getProject(), runSpec.getTaskSpec().getSecrets())
            )
                    .produce(run);
            default -> throw new IllegalArgumentException("Kind not recognized. Cannot retrieve the right Runner");
        };
    }

    @Override
    public K8sJobRunnable stop(Run runDTO) {
        return null;
    }

    @Override
    public RunDbtStatus onRunning(Run runDTO, K8sJobRunnable runnable) {
        return null;
    }

    @Override
    public RunDbtStatus onComplete(Run runDTO, K8sJobRunnable runnable) {
        return null;
    }

    @Override
    public RunDbtStatus onError(Run runDTO, K8sJobRunnable runnable) {
        return null;
    }

    @Override
    public RunDbtStatus onStopped(Run runDTO, K8sJobRunnable runnable) {
        return null;
    }
}
