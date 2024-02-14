package it.smartcommunitylabdhub.runtime.dbt.runners;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.infrastructure.Runner;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.runtime.dbt.DbtRuntime;
import it.smartcommunitylabdhub.runtime.dbt.specs.run.RunDbtSpec;
import java.util.*;

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
    private final Map<String, Set<String>> groupedSecrets;

    public DbtTransformRunner(String image, Map<String, Set<String>> groupedSecrets) {
        this.image = image;
        this.groupedSecrets = groupedSecrets;
    }

    @Override
    public K8sJobRunnable produce(Run runDTO) {
        // Retrieve information about RunDbtSpec
        RunDbtSpec runDbtSpec = RunDbtSpec.builder().build();
        runDbtSpec.configure(runDTO.getSpec());

        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(runDTO.getStatus());

        List<CoreEnv> coreEnvList = new ArrayList<>(
            List.of(new CoreEnv("PROJECT_NAME", runDTO.getProject()), new CoreEnv("RUN_ID", runDTO.getId()))
        );

        Optional.ofNullable(runDbtSpec.getTaskSpec().getEnvs()).ifPresent(coreEnvList::addAll);

        //TODO: Create runnable using information from Run completed spec.
        K8sJobRunnable k8sJobRunnable = K8sJobRunnable
            .builder()
            .runtime(DbtRuntime.RUNTIME)
            .task(TASK)
            .image(image)
            .command("python")
            .args(List.of("wrapper.py").toArray(String[]::new))
            .resources(runDbtSpec.getTaskSpec().getResources())
            .nodeSelector(runDbtSpec.getTaskSpec().getNodeSelector())
            .volumes(runDbtSpec.getTaskSpec().getVolumes())
            .secrets(groupedSecrets)
            .envs(coreEnvList)
            .labels(runDbtSpec.getTaskSpec().getLabels())
            .affinity(runDbtSpec.getTaskSpec().getAffinity())
            .tolerations(runDbtSpec.getTaskSpec().getTolerations())
            .state(statusFieldAccessor.getState())
            .build();

        k8sJobRunnable.setId(runDTO.getId());
        k8sJobRunnable.setProject(runDTO.getProject());

        return k8sJobRunnable;
    }
}
