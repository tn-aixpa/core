package it.smartcommunitylabdhub.runtime.container;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.infrastructure.Runnable;
import it.smartcommunitylabdhub.commons.infrastructure.Runtime;
import it.smartcommunitylabdhub.commons.models.base.RunStatus;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.services.SecretService;
import it.smartcommunitylabdhub.runtime.container.builders.ContainerDeployBuilder;
import it.smartcommunitylabdhub.runtime.container.builders.ContainerJobBuilder;
import it.smartcommunitylabdhub.runtime.container.builders.ContainerServeBuilder;
import it.smartcommunitylabdhub.runtime.container.runners.ContainerDeployRunner;
import it.smartcommunitylabdhub.runtime.container.runners.ContainerJobRunner;
import it.smartcommunitylabdhub.runtime.container.runners.ContainerServeRunner;
import it.smartcommunitylabdhub.runtime.container.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.runtime.container.specs.run.RunContainerSpec;
import it.smartcommunitylabdhub.runtime.container.specs.task.TaskDeploySpec;
import it.smartcommunitylabdhub.runtime.container.specs.task.TaskJobSpec;
import it.smartcommunitylabdhub.runtime.container.specs.task.TaskServeSpec;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

@RuntimeComponent(runtime = ContainerRuntime.RUNTIME)
public class ContainerRuntime implements Runtime<FunctionContainerSpec, RunContainerSpec, Runnable> {

    public static final String RUNTIME = "container";

    private final ContainerJobBuilder jobBuilder = new ContainerJobBuilder();
    private final ContainerDeployBuilder deployBuilder = new ContainerDeployBuilder();
    private final ContainerServeBuilder serveBuilder = new ContainerServeBuilder();

    @Autowired
    SecretService secretService;

    @Override
    public RunContainerSpec build(@NotNull Function function, @NotNull Task task, @NotNull Run run) {
        FunctionContainerSpec funSpec = new FunctionContainerSpec(function.getSpec());
        RunContainerSpec runSpec = new RunContainerSpec(run.getSpec());

        String kind = task.getKind();

        // Retrieve builder using task kind
        switch (kind) {
            case TaskDeploySpec.KIND -> {
                TaskDeploySpec taskDeploySpec = new TaskDeploySpec(task.getSpec());
                return deployBuilder.build(funSpec, taskDeploySpec, runSpec);
            }
            case TaskJobSpec.KIND -> {
                TaskJobSpec taskJobSpec = new TaskJobSpec(task.getSpec());
                return jobBuilder.build(funSpec, taskJobSpec, runSpec);
            }
            case TaskServeSpec.KIND -> {
                TaskServeSpec taskServeSpec = new TaskServeSpec(task.getSpec());
                return serveBuilder.build(funSpec, taskServeSpec, runSpec);
            }
            default -> throw new IllegalArgumentException(
                "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task."
            );
        }
    }

    @Override
    public Runnable run(@NotNull Run run) {
        RunContainerSpec runContainerSpec = new RunContainerSpec(run.getSpec());

        // Create string run accessor from task
        //TODO drop the utils and get the task accessor from the spec.
        RunSpecAccessor runAccessor = RunUtils.parseRun(runContainerSpec.getTask());

        return switch (runAccessor.getTask()) {
            case TaskDeploySpec.KIND -> new ContainerDeployRunner(
                runContainerSpec.getFunctionSpec(),
                secretService.groupSecrets(run.getProject(), runContainerSpec.getTaskDeploySpec().getSecrets())
            )
                .produce(run);
            case TaskJobSpec.KIND -> new ContainerJobRunner(
                runContainerSpec.getFunctionSpec(),
                secretService.groupSecrets(run.getProject(), runContainerSpec.getTaskJobSpec().getSecrets())
            )
                .produce(run);
            case TaskServeSpec.KIND -> new ContainerServeRunner(
                runContainerSpec.getFunctionSpec(),
                secretService.groupSecrets(run.getProject(), runContainerSpec.getTaskServeSpec().getSecrets())
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
