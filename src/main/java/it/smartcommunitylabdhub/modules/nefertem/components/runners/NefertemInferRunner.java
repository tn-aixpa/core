package it.smartcommunitylabdhub.modules.nefertem.components.runners;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.core.components.infrastructure.objects.CoreEnv;
import it.smartcommunitylabdhub.core.components.infrastructure.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.modules.nefertem.components.runtimes.NefertemRuntime;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.run.RunNefertemSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * DbtInferRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "dbt", task = "infer")
 */
public class NefertemInferRunner implements Runner {
    private static final String TASK = "infer";
    private final String image;
    private final RunDefaultFieldAccessor runDefaultFieldAccessor;
    private final Map<String, Set<String>> groupedSecrets;

    public NefertemInferRunner(String image,
                               RunDefaultFieldAccessor runDefaultFieldAccessor, Map<String, Set<String>> groupedSecrets) {
        this.image = image;
        this.runDefaultFieldAccessor = runDefaultFieldAccessor;
        this.groupedSecrets = groupedSecrets;
    }

    @Override
    public K8sJobRunnable produce(Run runDTO) {


        // Retrieve information about spec
        RunNefertemSpec runNefertemSpec = RunNefertemSpec.builder().build();
        runNefertemSpec.configure(runDTO.getSpec());


        List<CoreEnv> coreEnvList = new ArrayList<>(List.of(
                new CoreEnv("PROJECT_NAME", runDTO.getProject()),
                new CoreEnv("RUN_ID", runDTO.getId())
        ));
        if (runNefertemSpec.getTaskInferSpec().getEnvs() != null)
            coreEnvList.addAll(runNefertemSpec.getTaskInferSpec().getEnvs());

        //TODO: Create runnable using information from Run completed spec.
        K8sJobRunnable k8sJobRunnable = K8sJobRunnable.builder()
                .runtime(NefertemRuntime.RUNTIME)
                .task(TASK)
                .image(image)
                .command("python")
                .args(List.of("wrapper.py").toArray(String[]::new))
                .resources(runNefertemSpec.getTaskInferSpec().getResources())
                .nodeSelector(runNefertemSpec.getTaskInferSpec().getNodeSelector())
                .volumes(runNefertemSpec.getTaskInferSpec().getVolumes())
                .secrets(groupedSecrets)
                .envs(coreEnvList)
                .state(runDefaultFieldAccessor.getState())
                .build();

        k8sJobRunnable.setId(runDTO.getId());
        k8sJobRunnable.setProject(runDTO.getProject());

        return k8sJobRunnable;
    }

}
