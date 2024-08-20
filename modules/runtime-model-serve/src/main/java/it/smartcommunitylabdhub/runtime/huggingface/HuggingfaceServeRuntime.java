package it.smartcommunitylabdhub.runtime.huggingface;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.models.base.Executable;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.services.entities.SecretService;
import it.smartcommunitylabdhub.framework.k8s.base.K8sBaseRuntime;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import it.smartcommunitylabdhub.runtime.huggingface.specs.HuggingfaceServeFunctionSpec;
import it.smartcommunitylabdhub.runtime.huggingface.specs.HuggingfaceServeRunSpec;
import it.smartcommunitylabdhub.runtime.huggingface.specs.HuggingfaceServeTaskSpec;
import it.smartcommunitylabdhub.runtime.modelserve.specs.ModelServeRunStatus;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RuntimeComponent(runtime = HuggingfaceServeRuntime.RUNTIME)
public class HuggingfaceServeRuntime
    extends K8sBaseRuntime<HuggingfaceServeFunctionSpec, HuggingfaceServeRunSpec, ModelServeRunStatus, K8sRunnable> {

    public static final String RUNTIME = "huggingfaceserve";

    @Autowired
    private SecretService secretService;

    @Value("${runtime.huggingfaceserve.image}")
    private String image;

    public HuggingfaceServeRuntime() {
        super(HuggingfaceServeRunSpec.KIND);
    }

    @Override
    public HuggingfaceServeRunSpec build(@NotNull Executable function, @NotNull Task task, @NotNull Run run) {
        //check run kind
        if (!HuggingfaceServeRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(
                        String.valueOf(run.getKind()),
                        HuggingfaceServeRunSpec.KIND
                    )
            );
        }

        HuggingfaceServeFunctionSpec funSpec = HuggingfaceServeFunctionSpec.with(function.getSpec());
        HuggingfaceServeRunSpec runSpec = HuggingfaceServeRunSpec.with(run.getSpec());

        String kind = task.getKind();

        //build task spec as defined
        TaskBaseSpec taskSpec =
            switch (kind) {
                case HuggingfaceServeTaskSpec.KIND -> {
                    yield HuggingfaceServeTaskSpec.with(task.getSpec());
                }
                default -> throw new IllegalArgumentException(
                    "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task."
                );
            };

        //build run merging task spec overrides
        Map<String, Serializable> map = new HashMap<>();
        map.putAll(runSpec.toMap());
        taskSpec.toMap().forEach(map::putIfAbsent);

        HuggingfaceServeRunSpec serveSpec = HuggingfaceServeRunSpec.with(map);
        //ensure function is not modified
        serveSpec.setFunctionSpec(funSpec);

        return serveSpec;
    }

    @Override
    public K8sRunnable run(@NotNull Run run) {
        //check run kind
        if (!HuggingfaceServeRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(
                        String.valueOf(run.getKind()),
                        HuggingfaceServeRunSpec.KIND
                    )
            );
        }

        HuggingfaceServeRunSpec runSpec = HuggingfaceServeRunSpec.with(run.getSpec());

        // Create string run accessor from task
        RunSpecAccessor runAccessor = RunUtils.parseTask(runSpec.getTask());

        return switch (runAccessor.getTask()) {
            case HuggingfaceServeTaskSpec.KIND -> new HuggingfaceServeRunner(
                image,
                runSpec.getFunctionSpec(),
                secretService.groupSecrets(run.getProject(), runSpec.getTaskServeSpec().getSecrets()),
                k8sBuilderHelper
            )
                .produce(run);
            default -> throw new IllegalArgumentException("Kind not recognized. Cannot retrieve the right Runner");
        };
    }
}
