package it.smartcommunitylabdhub.runtime.mlrun;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.exceptions.CoreRuntimeException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.infrastructure.Runtime;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.commons.services.entities.SecretService;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.runtime.mlrun.builders.MlrunMlrunBuilder;
import it.smartcommunitylabdhub.runtime.mlrun.runners.MlrunMlrunRunner;
import it.smartcommunitylabdhub.runtime.mlrun.specs.function.FunctionMlrunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.run.RunMlrunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.task.TaskMlrunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.status.RunMlrunStatus;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@RuntimeComponent(runtime = MlrunRuntime.RUNTIME)
@Slf4j
public class MlrunRuntime implements Runtime<FunctionMlrunSpec, RunMlrunSpec, RunMlrunStatus, K8sJobRunnable> {

    public static final String RUNTIME = "mlrun";

    private final MlrunMlrunBuilder builder = new MlrunMlrunBuilder();

    @Autowired
    SecretService secretService;

    @Autowired(required = false)
    private RunnableStore<K8sJobRunnable> jobRunnableStore;

    @Value("${runtime.mlrun.image}")
    private String image;

    @Override
    public RunMlrunSpec build(@NotNull Function function, @NotNull Task task, @NotNull Run run) {
        //check run kind
        if (!RunMlrunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), RunMlrunSpec.KIND)
            );
        }

        FunctionMlrunSpec functionSpec = new FunctionMlrunSpec(function.getSpec());
        RunMlrunSpec runSpec = new RunMlrunSpec(run.getSpec());

        String kind = task.getKind();
        // Retrieve builder using task kind
        switch (kind) {
            case TaskMlrunSpec.KIND -> {
                TaskMlrunSpec taskMlrunSpec = new TaskMlrunSpec(task.getSpec());
                return builder.build(functionSpec, taskMlrunSpec, runSpec);
            }
            default -> throw new IllegalArgumentException(
                "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task."
            );
        }
    }

    @Override
    public K8sJobRunnable run(Run run) {
        //check run kind
        if (!RunMlrunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), RunMlrunSpec.KIND)
            );
        }

        // Create spec for run
        RunMlrunSpec runSpec = new RunMlrunSpec(run.getSpec());

        // Create string run accessor from task
        RunSpecAccessor runAccessor = RunUtils.parseTask(runSpec.getTask());

        return switch (runAccessor.getTask()) {
            case TaskMlrunSpec.KIND -> new MlrunMlrunRunner(
                image,
                secretService.groupSecrets(run.getProject(), runSpec.getTaskSpec().getSecrets())
            )
                .produce(run);
            default -> throw new IllegalArgumentException("Kind not recognized. Cannot retrieve the right Runner");
        };
    }

    @Override
    public K8sJobRunnable stop(Run run) {
        //check run kind
        if (!RunMlrunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), RunMlrunSpec.KIND)
            );
        }

        if (jobRunnableStore == null) {
            throw new CoreRuntimeException("Job Store is not available");
        }

        try {
            K8sJobRunnable k8sJobRunnable = jobRunnableStore.find(run.getId());
            if (k8sJobRunnable == null) {
                throw new NoSuchEntityException("JobRunnable not found");
            }

            //set state to STOP to signal framework to stop the runnable
            k8sJobRunnable.setState(State.STOP.name());

            return k8sJobRunnable;
        } catch (StoreException e) {
            log.error("Error stopping run", e);
            throw new NoSuchEntityException("Error stopping run", e);
        }
    }

    @Override
    public K8sJobRunnable delete(Run run) {
        //check run kind
        if (!RunMlrunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), RunMlrunSpec.KIND)
            );
        }

        if (jobRunnableStore == null) {
            throw new CoreRuntimeException("Job Store is not available");
        }
        try {
            K8sJobRunnable k8sJobRunnable = jobRunnableStore.find(run.getId());
            if (k8sJobRunnable == null) {
                throw new NoSuchEntityException("JobRunnable not found");
            }

            //set state to DELETING to signal framework to delete the runnable
            k8sJobRunnable.setState(State.DELETING.name());

            return k8sJobRunnable;
        } catch (StoreException e) {
            log.error("Error stopping run", e);
            throw new NoSuchEntityException("Error stopping run", e);
        }
    }

    @Override
    public RunMlrunStatus onRunning(Run run, RunRunnable runnable) {
        return null;
    }

    @Override
    public RunMlrunStatus onComplete(Run run, RunRunnable runnable) {
        return null;
    }

    @Override
    public RunMlrunStatus onError(Run run, RunRunnable runnable) {
        return null;
    }

    @Override
    public RunMlrunStatus onStopped(Run run, RunRunnable runnable) {
        return null;
    }

    @Override
    public RunMlrunStatus onDeleted(Run run, RunRunnable runnable) {
        return null;
    }
}
