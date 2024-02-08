package it.smartcommunitylabdhub.core.components.infrastructure.runtimes;

import it.smartcommunitylabdhub.core.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.BuilderFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.Runnable;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.RunnerFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runtimes.Runtime;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.task.specs.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Getter
public abstract class BaseRuntime<F extends FunctionBaseSpec, S extends RunBaseSpec, R extends Runnable> implements Runtime<F, S, R> {

    protected final BuilderFactory builderFactory;
    protected final RunnerFactory runnerFactory;
    protected Map<String, ? extends Runner> runners;
    protected Map<String, ? extends Builder<
            ? extends FunctionBaseSpec,
            ? extends TaskBaseSpec,
            ? extends RunBaseSpec>> builders;
    private String runtime;

    protected BaseRuntime(BuilderFactory builderFactory, RunnerFactory runnerFactory) {
        this.builderFactory = builderFactory;
        this.runnerFactory = runnerFactory;

        // Retrieve runtime
        // Retrieve the runtime value from the subclass if it has a @RuntimeComponent annotation
        RuntimeComponent runtimeComponentAnnotation =
                getClass().getAnnotation(RuntimeComponent.class);
        if (runtimeComponentAnnotation != null) {
            runtime = runtimeComponentAnnotation.runtime();

            // register all builders and runners
            builders = builderFactory.getBuilders(runtime);
            runners = runnerFactory.getRunners(runtime);

        } else {
            log.warn("No @RuntimeComponent annotation found on the subclass.");
        }
    }


    @SuppressWarnings("unchecked")
    public <R extends Runner> R getRunner(String task) {

        return Optional.ofNullable((R) runners.get(runtime + "+" + task))
                .orElseThrow(() -> new CoreException(
                        ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                        "Cannot find registered Runner for <" + runtime + "+" + task + ">",
                        HttpStatus.INTERNAL_SERVER_ERROR
                ));

    }

    @SuppressWarnings("unchecked")
    public <B extends Builder<
            ? extends FunctionBaseSpec,
            ? extends K8sTaskBaseSpec,
            ? extends RunBaseSpec>> B getBuilder(String task) {

        return Optional.ofNullable((B) builders.get(runtime + "+" + task))
                .orElseThrow(() -> new CoreException(
                        ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                        "Cannot find registered Builder for <" + runtime + "+" + task + ">",
                        HttpStatus.INTERNAL_SERVER_ERROR
                ));
    }
}
