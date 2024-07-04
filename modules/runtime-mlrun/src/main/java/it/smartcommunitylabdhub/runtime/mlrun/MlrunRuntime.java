package it.smartcommunitylabdhub.runtime.mlrun;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.accessors.spec.TaskSpecAccessor;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.exceptions.CoreRuntimeException;
import it.smartcommunitylabdhub.commons.models.base.Executable;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.models.utils.TaskUtils;
import it.smartcommunitylabdhub.commons.services.entities.SecretService;
import it.smartcommunitylabdhub.framework.k8s.base.K8sBaseRuntime;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.runtime.mlrun.runners.MlrunBuildRunner;
import it.smartcommunitylabdhub.runtime.mlrun.runners.MlrunJobRunner;
import it.smartcommunitylabdhub.runtime.mlrun.specs.MlrunBuildTaskSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.MlrunFunctionSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.MlrunJobTaskSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.MlrunRunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.status.RunMlrunStatus;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

@RuntimeComponent(runtime = MlrunRuntime.RUNTIME)
@Slf4j
public class MlrunRuntime extends K8sBaseRuntime<MlrunFunctionSpec, MlrunRunSpec, RunMlrunStatus, K8sJobRunnable> {

    public static final String RUNTIME = "mlrun";

    @Autowired
    SecretService secretService;

    @Value("${runtime.mlrun.image}")
    private String image;

    @Value("${mlrun.base-image}")
    private String baseImage;

    @Value("${mlrun.image-prefix}")
    private String imagePrefix;

    @Value("${mlrun.image-registry:}")
    private String imageRegistry;

    public MlrunRuntime() {
        super(MlrunRunSpec.KIND);
    }

    @Override
    public MlrunRunSpec build(@NotNull Executable function, @NotNull Task task, @NotNull Run run) {
        //check run kind
        if (!MlrunRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), MlrunRunSpec.KIND)
            );
        }

        MlrunFunctionSpec functionSpec = new MlrunFunctionSpec(function.getSpec());
        MlrunRunSpec runSpec = new MlrunRunSpec(run.getSpec());

        String kind = task.getKind();

        //build task spec as defined
        TaskBaseSpec taskSpec =
            switch (kind) {
                case MlrunJobTaskSpec.KIND -> {
                    yield new MlrunJobTaskSpec(task.getSpec());
                }
                case MlrunBuildTaskSpec.KIND -> {
                    yield new MlrunBuildTaskSpec(task.getSpec());
                }
                default -> throw new IllegalArgumentException(
                    "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task."
                );
            };

        //build run merging task spec overrides
        Map<String, Serializable> map = new HashMap<>();
        map.putAll(runSpec.toMap());
        taskSpec.toMap().forEach(map::putIfAbsent);

        MlrunRunSpec mlrunSpec = new MlrunRunSpec(map);
        //ensure function is not modified
        mlrunSpec.setFunctionSpec(functionSpec);

        return mlrunSpec;
    }

    private String createTargetImage(String name, String id) {
        return (StringUtils.hasText(imageRegistry) ? imageRegistry + "/" : "") + imagePrefix + "-" + name + ":" + id;
    }

    @Override
    public K8sJobRunnable run(Run run) {
        //check run kind
        if (!MlrunRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), MlrunRunSpec.KIND)
            );
        }

        // Create spec for run
        MlrunRunSpec runSpec = new MlrunRunSpec(run.getSpec());

        // Create string run accessor from task
        RunSpecAccessor runAccessor = RunUtils.parseTask(runSpec.getTask());

        return switch (runAccessor.getTask()) {
            case MlrunJobTaskSpec.KIND -> {
                MlrunJobTaskSpec taskSpec = runSpec.getJobSpec();
                if (taskSpec == null) {
                    throw new CoreRuntimeException("null or empty task definition");
                }

                yield new MlrunJobRunner(image, secretService.groupSecrets(run.getProject(), taskSpec.getSecrets()))
                    .produce(run);
            }
            case MlrunBuildTaskSpec.KIND -> {
                MlrunBuildTaskSpec taskSpec = runSpec.getBuildSpec();
                TaskSpecAccessor accessor = TaskUtils.parseFunction(taskSpec.getFunction());
                taskSpec.setTargetImage(createTargetImage(accessor.getFunction(), accessor.getVersion()));

                if (taskSpec == null) {
                    throw new CoreRuntimeException("null or empty task definition");
                }

                yield new MlrunBuildRunner(image, secretService.groupSecrets(run.getProject(), taskSpec.getSecrets()))
                    .produce(run);
            }
            default -> throw new IllegalArgumentException("Kind not recognized. Cannot retrieve the right Runner");
        };
    }
}
