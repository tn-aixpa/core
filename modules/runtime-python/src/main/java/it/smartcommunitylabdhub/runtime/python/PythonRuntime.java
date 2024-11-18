package it.smartcommunitylabdhub.runtime.python;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.models.base.Executable;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.services.entities.FunctionService;
import it.smartcommunitylabdhub.commons.services.entities.SecretService;
import it.smartcommunitylabdhub.framework.k8s.base.K8sBaseRuntime;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import it.smartcommunitylabdhub.framework.kaniko.runnables.K8sKanikoRunnable;
import it.smartcommunitylabdhub.runtime.python.runners.PythonBuildRunner;
import it.smartcommunitylabdhub.runtime.python.runners.PythonJobRunner;
import it.smartcommunitylabdhub.runtime.python.runners.PythonServeRunner;
import it.smartcommunitylabdhub.runtime.python.specs.PythonBuildTaskSpec;
import it.smartcommunitylabdhub.runtime.python.specs.PythonFunctionSpec;
import it.smartcommunitylabdhub.runtime.python.specs.PythonJobTaskSpec;
import it.smartcommunitylabdhub.runtime.python.specs.PythonRunSpec;
import it.smartcommunitylabdhub.runtime.python.specs.PythonRunStatus;
import it.smartcommunitylabdhub.runtime.python.specs.PythonServeTaskSpec;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RuntimeComponent(runtime = PythonRuntime.RUNTIME)
public class PythonRuntime extends K8sBaseRuntime<PythonFunctionSpec, PythonRunSpec, PythonRunStatus, K8sRunnable> {

    public static final String RUNTIME = "python";

    @Autowired
    private SecretService secretService;

    @Autowired
    private FunctionService functionService;

    @Autowired
    @Qualifier("pythonImages")
    private Map<String, String> images;

    @Value("${runtime.python.command}")
    private String command;

    public PythonRuntime() {
        super(PythonRunSpec.KIND);
    }

    @Override
    public PythonRunSpec build(@NotNull Executable function, @NotNull Task task, @NotNull Run run) {
        //check run kind
        if (!PythonRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), PythonRunSpec.KIND)
            );
        }

        PythonFunctionSpec funSpec = new PythonFunctionSpec(function.getSpec());
        PythonRunSpec runSpec = new PythonRunSpec(run.getSpec());

        String kind = task.getKind();

        //build task spec as defined
        TaskBaseSpec taskSpec =
            switch (kind) {
                case PythonJobTaskSpec.KIND -> {
                    yield new PythonJobTaskSpec(task.getSpec());
                }
                case PythonServeTaskSpec.KIND -> {
                    yield new PythonServeTaskSpec(task.getSpec());
                }
                case PythonBuildTaskSpec.KIND -> {
                    yield new PythonBuildTaskSpec(task.getSpec());
                }
                default -> throw new IllegalArgumentException(
                    "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task."
                );
            };

        //build run merging task spec overrides
        Map<String, Serializable> map = new HashMap<>();
        map.putAll(runSpec.toMap());
        taskSpec.toMap().forEach(map::putIfAbsent);

        PythonRunSpec pythonSpec = new PythonRunSpec(map);
        //ensure function is not modified
        pythonSpec.setFunctionSpec(funSpec);

        return pythonSpec;
    }

    @Override
    public K8sRunnable run(@NotNull Run run) {
        //check run kind
        if (!PythonRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), PythonRunSpec.KIND)
            );
        }

        PythonRunSpec runPythonSpec = new PythonRunSpec(run.getSpec());

        // Create string run accessor from task
        RunSpecAccessor runAccessor = RunSpecAccessor.with(run.getSpec());

        return switch (runAccessor.getTask()) {
            case PythonJobTaskSpec.KIND -> new PythonJobRunner(
                images.get(runPythonSpec.getFunctionSpec().getPythonVersion().name()),
                command,
                runPythonSpec.getFunctionSpec(),
                secretService.getSecretData(run.getProject(), runPythonSpec.getTaskJobSpec().getSecrets()),
                k8sBuilderHelper
            )
                .produce(run);
            case PythonServeTaskSpec.KIND -> new PythonServeRunner(
                images.get(runPythonSpec.getFunctionSpec().getPythonVersion().name()),
                command,
                runPythonSpec.getFunctionSpec(),
                secretService.getSecretData(run.getProject(), runPythonSpec.getTaskJobSpec().getSecrets()),
                k8sBuilderHelper
            )
                .produce(run);
            case PythonBuildTaskSpec.KIND -> new PythonBuildRunner(
                images.get(runPythonSpec.getFunctionSpec().getPythonVersion().name()),
                command,
                runPythonSpec.getFunctionSpec(),
                secretService.getSecretData(run.getProject(), runPythonSpec.getTaskBuildSpec().getSecrets()),
                k8sBuilderHelper
            )
                .produce(run);
            default -> throw new IllegalArgumentException("Kind not recognized. Cannot retrieve the right Runner");
        };
    }

    @Override
    public PythonRunStatus onComplete(Run run, RunRunnable runnable) {
        RunSpecAccessor runAccessor = RunSpecAccessor.with(run.getSpec());

        //update image name after build
        if (runnable instanceof K8sKanikoRunnable) {
            String image = ((K8sKanikoRunnable) runnable).getImage();

            String functionId = runAccessor.getFunctionId();
            Function function = functionService.getFunction(functionId);

            log.debug("update function {} spec to use built image: {}", functionId, image);

            PythonFunctionSpec funSpec = new PythonFunctionSpec(function.getSpec());
            if (!image.equals(funSpec.getImage())) {
                funSpec.setImage(image);
                function.setSpec(funSpec.toMap());
                functionService.updateFunction(functionId, function, true);
            }
        }

        return null;
    }
}
