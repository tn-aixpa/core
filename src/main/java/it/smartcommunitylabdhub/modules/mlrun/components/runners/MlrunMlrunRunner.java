package it.smartcommunitylabdhub.modules.mlrun.components.runners;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.core.components.infrastructure.objects.CoreEnv;
import it.smartcommunitylabdhub.core.components.infrastructure.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.modules.mlrun.components.runtimes.MlrunRuntime;
import it.smartcommunitylabdhub.modules.mlrun.models.specs.run.RunMlrunSpec;

import java.util.ArrayList;
import java.util.List;


/**
 * MlrunMlrunRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "mlrun", task = "mlrun")
 */
public class MlrunMlrunRunner implements Runner {

    private final static String TASK = "mlrun";

    private final String image;
    private final RunDefaultFieldAccessor runDefaultFieldAccessor;

    public MlrunMlrunRunner(String image,
                            RunDefaultFieldAccessor runDefaultFieldAccessor) {
        this.image = image;
        this.runDefaultFieldAccessor = runDefaultFieldAccessor;
    }

    @Override
    public K8sJobRunnable produce(Run runDTO) {

        // Retrieve information about RunMlrunSpec
        RunMlrunSpec runMlrunSpec = RunMlrunSpec.builder().build();
        runMlrunSpec.configure(runDTO.getSpec());


        List<CoreEnv> coreEnvList = new ArrayList<>(List.of(
                new CoreEnv("PROJECT_NAME", runDTO.getProject()),
                new CoreEnv("RUN_ID", runDTO.getId())
        ));

        //TODO: Create runnable using information from Run completed spec.
        K8sJobRunnable k8sJobRunnable = K8sJobRunnable.builder()
                .runtime(MlrunRuntime.RUNTIME)
                .task(TASK)
                .image(image)
                .command("python")
                .args(List.of("wrapper.py").toArray(String[]::new))
                .envs(coreEnvList)
                .state(runDefaultFieldAccessor.getState())
                .build();

        k8sJobRunnable.setId(runDTO.getId());
        k8sJobRunnable.setProject(runDTO.getProject());

        return k8sJobRunnable;


    }
}
