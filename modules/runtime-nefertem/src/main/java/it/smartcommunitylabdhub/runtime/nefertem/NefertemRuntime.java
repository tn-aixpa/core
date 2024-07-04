package it.smartcommunitylabdhub.runtime.nefertem;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.models.base.Executable;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.services.entities.SecretService;
import it.smartcommunitylabdhub.framework.k8s.base.K8sBaseRuntime;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.runtime.nefertem.runners.NefertemInferRunner;
import it.smartcommunitylabdhub.runtime.nefertem.runners.NefertemMetricRunner;
import it.smartcommunitylabdhub.runtime.nefertem.runners.NefertemProfileRunner;
import it.smartcommunitylabdhub.runtime.nefertem.runners.NefertemValidateRunner;
import it.smartcommunitylabdhub.runtime.nefertem.specs.NefertemFunctionSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.NefertemInferTaskSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.NefertemMetricTaskSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.NefertemProfileTaskSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.NefertemRunSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.NefertemRunStatus;
import it.smartcommunitylabdhub.runtime.nefertem.specs.NefertemValidateTaskSpec;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@RuntimeComponent(runtime = NefertemRuntime.RUNTIME)
@Slf4j
public class NefertemRuntime
    extends K8sBaseRuntime<NefertemFunctionSpec, NefertemRunSpec, NefertemRunStatus, K8sJobRunnable> {

    public static final String RUNTIME = "nefertem";

    @Autowired
    SecretService secretService;

    @Value("${runtime.nefertem.image}")
    private String image;

    public NefertemRuntime() {
        super(NefertemRunSpec.KIND);
    }

    @Override
    public NefertemRunSpec build(@NotNull Executable function, @NotNull Task task, @NotNull Run run) {
        //check run kind
        if (!NefertemRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), NefertemRunSpec.KIND)
            );
        }

        NefertemFunctionSpec functionSpec = new NefertemFunctionSpec(function.getSpec());
        NefertemRunSpec runSpec = new NefertemRunSpec(run.getSpec());

        String kind = task.getKind();

        //build task spec as defined
        TaskBaseSpec taskSpec =
            switch (kind) {
                case NefertemInferTaskSpec.KIND -> {
                    yield new NefertemInferTaskSpec(task.getSpec());
                }
                case NefertemValidateTaskSpec.KIND -> {
                    yield new NefertemValidateTaskSpec(task.getSpec());
                }
                case NefertemProfileTaskSpec.KIND -> {
                    yield new NefertemProfileTaskSpec(task.getSpec());
                }
                case NefertemMetricTaskSpec.KIND -> {
                    yield new NefertemMetricTaskSpec(task.getSpec());
                }
                default -> throw new IllegalArgumentException(
                    "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task."
                );
            };

        //build run merging task spec overrides
        Map<String, Serializable> map = new HashMap<>();
        map.putAll(runSpec.toMap());
        taskSpec.toMap().forEach(map::putIfAbsent);

        NefertemRunSpec nefertemSpec = new NefertemRunSpec(map);
        //ensure function is not modified
        nefertemSpec.setFunctionSpec(functionSpec);

        return nefertemSpec;
    }

    @Override
    public K8sJobRunnable run(Run run) {
        //check run kind
        if (!NefertemRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), NefertemRunSpec.KIND)
            );
        }

        // Create spec for run
        NefertemRunSpec runSpec = new NefertemRunSpec(run.getSpec());

        // Create string run accessor from task
        RunSpecAccessor runAccessor = RunUtils.parseTask(runSpec.getTask());

        return switch (runAccessor.getTask()) {
            case NefertemInferTaskSpec.KIND -> new NefertemInferRunner(
                image,
                secretService.groupSecrets(run.getProject(), runSpec.getTaskInferSpec().getSecrets())
            )
                .produce(run);
            case NefertemValidateTaskSpec.KIND -> new NefertemValidateRunner(
                image,
                secretService.groupSecrets(run.getProject(), runSpec.getTaskValidateSpec().getSecrets())
            )
                .produce(run);
            case NefertemProfileTaskSpec.KIND -> new NefertemProfileRunner(
                image,
                secretService.groupSecrets(run.getProject(), runSpec.getTaskProfileSpec().getSecrets())
            )
                .produce(run);
            case NefertemMetricTaskSpec.KIND -> new NefertemMetricRunner(
                image,
                secretService.groupSecrets(run.getProject(), runSpec.getTaskMetricSpec().getSecrets())
            )
                .produce(run);
            default -> throw new IllegalArgumentException("Kind not recognized. Cannot retrieve the right Runner");
        };
    }
}
