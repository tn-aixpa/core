package it.smartcommunitylabdhub.runtime.kfp;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.infrastructure.Runnable;
import it.smartcommunitylabdhub.commons.infrastructure.Runtime;
import it.smartcommunitylabdhub.commons.models.base.RunStatus;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.services.ProjectSecretService;
import it.smartcommunitylabdhub.runtime.kfp.builders.KFPPipelineBuilder;
import it.smartcommunitylabdhub.runtime.kfp.runners.KFPPipelineRunner;
import it.smartcommunitylabdhub.runtime.kfp.specs.function.FunctionKFPSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.run.RunKFPSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.task.TaskPipelineSpec;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@RuntimeComponent(runtime = KFPRuntime.RUNTIME)
public class KFPRuntime implements Runtime<FunctionKFPSpec, RunKFPSpec, Runnable> {

    public static final String RUNTIME = "kfp";

    private final KFPPipelineBuilder pipelineBuilder = new KFPPipelineBuilder();

    @Autowired
    ProjectSecretService secretService;

    @Value("${runtime.kfp.image}")
    private String image;

    @Override
    public RunKFPSpec build(@NotNull Function function, @NotNull Task task, @NotNull Run run) {
        FunctionKFPSpec funSpec = new FunctionKFPSpec(function.getSpec());
        RunKFPSpec runSpec = new RunKFPSpec(run.getSpec());

        String kind = task.getKind();

        // Retrieve builder using task kind
        switch (kind) {
            case TaskPipelineSpec.KIND -> {
                TaskPipelineSpec taskPipelineSpec = new TaskPipelineSpec(task.getSpec());
                return pipelineBuilder.build(funSpec, taskPipelineSpec, runSpec);
            }
            default -> throw new IllegalArgumentException(
                "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task."
            );
        }
    }

    @Override
    public Runnable run(@NotNull Run run) {
        RunKFPSpec runKfpSpec = new RunKFPSpec(run.getSpec());

        // Create string run accessor from task
        //TODO drop the utils and get the task accessor from the spec.
        RunSpecAccessor runAccessor = RunUtils.parseRun(runKfpSpec.getTask());

        return switch (runAccessor.getTask()) {
            case TaskPipelineSpec.KIND -> new KFPPipelineRunner(
                image,
                secretService.groupSecrets(run.getProject(), runKfpSpec.getTaskPipelineSpec().getSecrets())
            )
                .produce(run);
            default -> throw new IllegalArgumentException("Kind not recognized. Cannot retrieve the right Runner");
        };
    }

    @Override
    public RunStatus parse() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parse'");
    }
}
