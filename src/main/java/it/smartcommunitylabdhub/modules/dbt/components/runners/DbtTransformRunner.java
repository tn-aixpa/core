package it.smartcommunitylabdhub.modules.dbt.components.runners;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.core.components.infrastructure.objects.CoreEnv;
import it.smartcommunitylabdhub.core.components.infrastructure.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.modules.dbt.components.runtimes.DbtRuntime;
import it.smartcommunitylabdhub.modules.dbt.models.specs.run.RunDbtSpec;

import java.util.ArrayList;
import java.util.List;


/**
 * DbtTransformRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "dbt", task = "transform")
 */
public class DbtTransformRunner implements Runner {
    private static final String TASK = "transform";

    private final String image;
    private final RunDefaultFieldAccessor runDefaultFieldAccessor;

    public DbtTransformRunner(String image,
                              RunDefaultFieldAccessor runDefaultFieldAccessor) {
        this.image = image;
        this.runDefaultFieldAccessor = runDefaultFieldAccessor;
    }

    @Override
    public K8sJobRunnable produce(Run runDTO) {

        // Retrieve information about RunDbtSpec
        RunDbtSpec runDbtSpec = RunDbtSpec.builder().build();
        runDbtSpec.configure(runDTO.getSpec());


        List<CoreEnv> coreEnvList = new ArrayList<>(List.of(
                new CoreEnv("PROJECT_NAME", runDTO.getProject()),
                new CoreEnv("RUN_ID", runDTO.getId())
        ));
        if (runDbtSpec.getTaskTransformSpec().getEnvs() != null) 
                coreEnvList.addAll(runDbtSpec.getTaskTransformSpec().getEnvs());

        //TODO: Create runnable using information from Run completed spec.
        K8sJobRunnable k8sJobRunnable = K8sJobRunnable.builder()
                .runtime(DbtRuntime.RUNTIME)
                .task(TASK)
                .image(image)
                .command("python")
                .args(List.of("wrapper.py").toArray(String[]::new))
                .resources(runDbtSpec.getTaskTransformSpec().getResources())
                .nodeSelector(runDbtSpec.getTaskTransformSpec().getNodeSelector())
                .volumes(runDbtSpec.getTaskTransformSpec().getVolumes())
                //.secrets(runDbtSpec.getTaskTransformSpec().getSecrets())
                .envs(coreEnvList)
                .state(runDefaultFieldAccessor.getState())
                .build();

        k8sJobRunnable.setId(runDTO.getId());
        k8sJobRunnable.setProject(runDTO.getProject());

        return k8sJobRunnable;
    }
}
