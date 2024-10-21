package it.smartcommunitylabdhub.runtime.dbt;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.exceptions.CoreRuntimeException;
import it.smartcommunitylabdhub.commons.models.base.Executable;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.services.entities.SecretService;
import it.smartcommunitylabdhub.framework.k8s.base.K8sBaseRuntime;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.runtime.dbt.runners.DbtTransformRunner;
import it.smartcommunitylabdhub.runtime.dbt.specs.DbtFunctionSpec;
import it.smartcommunitylabdhub.runtime.dbt.specs.DbtRunSpec;
import it.smartcommunitylabdhub.runtime.dbt.specs.DbtRunStatus;
import it.smartcommunitylabdhub.runtime.dbt.specs.DbtTransformSpec;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@RuntimeComponent(runtime = DbtRuntime.RUNTIME)
@Slf4j
public class DbtRuntime extends K8sBaseRuntime<DbtFunctionSpec, DbtRunSpec, DbtRunStatus, K8sJobRunnable> {

    public static final String RUNTIME = "dbt";

    @Autowired
    SecretService secretService;

    @Value("${runtime.dbt.image}")
    private String image;

    public DbtRuntime() {
        super(DbtRunSpec.KIND);
    }

    @Override
    public DbtRunSpec build(@NotNull Executable function, @NotNull Task task, @NotNull Run run) {
        //check run kind
        if (!DbtRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), DbtRunSpec.KIND)
            );
        }

        log.debug("build run spec for run {}", run.getId());

        DbtFunctionSpec functionSpec = new DbtFunctionSpec(function.getSpec());
        DbtRunSpec runSpec = new DbtRunSpec(run.getSpec());

        String kind = task.getKind();

        //build task spec as defined
        TaskBaseSpec taskSpec =
            switch (kind) {
                case DbtTransformSpec.KIND -> {
                    yield new DbtTransformSpec(task.getSpec());
                }
                default -> throw new IllegalArgumentException(
                    "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task."
                );
            };

        //build run merging task spec overrides
        Map<String, Serializable> map = new HashMap<>();
        map.putAll(runSpec.toMap());
        taskSpec.toMap().forEach(map::putIfAbsent);

        DbtRunSpec dbtRunSpec = new DbtRunSpec(map);
        //ensure function is not modified
        dbtRunSpec.setFunctionSpec(functionSpec);

        return dbtRunSpec;
    }

    @Override
    public K8sJobRunnable run(Run run) {
        //check run kind
        if (!DbtRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), DbtRunSpec.KIND)
            );
        }

        log.debug("build runnable for run {}", run.getId());

        // Crete spec for run
        DbtRunSpec runSpec = new DbtRunSpec(run.getSpec());

        // Create string run accessor from task
        //TODO drop the utils and get the task accessor from the spec.
        RunSpecAccessor runAccessor = RunUtils.parseTask(runSpec.getTask());

        return switch (runAccessor.getTask()) {
            case DbtTransformSpec.KIND -> {
                DbtTransformSpec taskSpec = runSpec.getTaskSpec();
                if (taskSpec == null) {
                    throw new CoreRuntimeException("null or empty task definition");
                }

                yield new DbtTransformRunner(
                    image,
                    secretService.getSecretData(run.getProject(), taskSpec.getSecrets())
                )
                    .produce(run);
            }
            default -> throw new IllegalArgumentException("Kind not recognized. Cannot retrieve the right Runner");
        };
    }
}
