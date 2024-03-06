package it.smartcommunitylabdhub.runtime.nefertem;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.infrastructure.Runtime;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.services.entities.SecretService;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.runtime.nefertem.builders.NefertemInferBuilder;
import it.smartcommunitylabdhub.runtime.nefertem.builders.NefertemMetricBuilder;
import it.smartcommunitylabdhub.runtime.nefertem.builders.NefertemProfileBuilder;
import it.smartcommunitylabdhub.runtime.nefertem.builders.NefertemValidateBuilder;
import it.smartcommunitylabdhub.runtime.nefertem.runners.NefertemInferRunner;
import it.smartcommunitylabdhub.runtime.nefertem.runners.NefertemMetricRunner;
import it.smartcommunitylabdhub.runtime.nefertem.runners.NefertemProfileRunner;
import it.smartcommunitylabdhub.runtime.nefertem.runners.NefertemValidateRunner;
import it.smartcommunitylabdhub.runtime.nefertem.specs.function.FunctionNefertemSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.run.RunNefertemSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.task.TaskInferSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.task.TaskMetricSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.task.TaskProfileSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.task.TaskValidateSpec;
import it.smartcommunitylabdhub.runtime.nefertem.status.RunNefertemStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@RuntimeComponent(runtime = NefertemRuntime.RUNTIME)
public class NefertemRuntime implements Runtime<FunctionNefertemSpec, RunNefertemSpec, RunNefertemStatus, K8sJobRunnable> {

    public static final String RUNTIME = "nefertem";
    private final NefertemInferBuilder inferBuilder = new NefertemInferBuilder();
    private final NefertemMetricBuilder metricBuilder = new NefertemMetricBuilder();
    private final NefertemValidateBuilder validateBuilder = new NefertemValidateBuilder();
    private final NefertemProfileBuilder profileBuilder = new NefertemProfileBuilder();

    @Autowired
    SecretService secretService;

    @Value("${runtime.nefertem.image}")
    private String image;

    @Override
    public RunNefertemSpec build(@NotNull Function function, @NotNull Task task, @NotNull Run run) {
        FunctionNefertemSpec functionSpec = new FunctionNefertemSpec(function.getSpec());
        RunNefertemSpec runSpec = new RunNefertemSpec(run.getSpec());

        String kind = task.getKind();

        // Retrieve builder using task kind
        switch (kind) {
            case TaskInferSpec.KIND -> {
                TaskInferSpec taskInferSpec = new TaskInferSpec(task.getSpec());
                return inferBuilder.build(functionSpec, taskInferSpec, runSpec);
            }
            case TaskValidateSpec.KIND -> {
                TaskValidateSpec taskValidateSpec = new TaskValidateSpec(task.getSpec());
                return validateBuilder.build(functionSpec, taskValidateSpec, runSpec);
            }
            case TaskProfileSpec.KIND -> {
                TaskProfileSpec taskProfileSpec = new TaskProfileSpec(task.getSpec());
                return profileBuilder.build(functionSpec, taskProfileSpec, runSpec);
            }
            case TaskMetricSpec.KIND -> {
                TaskMetricSpec taskMetricSpec = new TaskMetricSpec(task.getSpec());
                return metricBuilder.build(functionSpec, taskMetricSpec, runSpec);
            }
            default -> throw new IllegalArgumentException(
                    "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task."
            );
        }
    }

    @Override
    public K8sJobRunnable run(Run run) {
        RunNefertemSpec runSpec = new RunNefertemSpec(run.getSpec());

        // Create string run accessor from task
        //TODO drop the utils and get the task accessor from the spec.
        RunSpecAccessor runAccessor = RunUtils.parseTask(runSpec.getTask());

        return switch (runAccessor.getTask()) {
            case TaskInferSpec.KIND -> new NefertemInferRunner(
                    image,
                    secretService.groupSecrets(run.getProject(), runSpec.getTaskInferSpec().getSecrets())
            )
                    .produce(run);
            case TaskValidateSpec.KIND -> new NefertemValidateRunner(
                    image,
                    secretService.groupSecrets(run.getProject(), runSpec.getTaskValidateSpec().getSecrets())
            )
                    .produce(run);
            case TaskProfileSpec.KIND -> new NefertemProfileRunner(
                    image,
                    secretService.groupSecrets(run.getProject(), runSpec.getTaskProfileSpec().getSecrets())
            )
                    .produce(run);
            case TaskMetricSpec.KIND -> new NefertemMetricRunner(
                    image,
                    secretService.groupSecrets(run.getProject(), runSpec.getTaskMetricSpec().getSecrets())
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
    public RunNefertemStatus onRunning(Run runDTO, K8sJobRunnable runnable) {
        return null;
    }

    @Override
    public RunNefertemStatus onComplete(Run runDTO, K8sJobRunnable runnable) {
        return null;
    }

    @Override
    public RunNefertemStatus onError(Run runDTO, K8sJobRunnable runnable) {
        return null;
    }

    @Override
    public RunNefertemStatus onStopped(Run runDTO, K8sJobRunnable runnable) {
        return null;
    }
}
