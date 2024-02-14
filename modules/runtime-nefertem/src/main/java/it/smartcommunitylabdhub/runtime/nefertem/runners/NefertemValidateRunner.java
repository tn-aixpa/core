package it.smartcommunitylabdhub.runtime.nefertem.runners;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.infrastructure.Runner;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.runtime.nefertem.NefertemRuntime;
import it.smartcommunitylabdhub.runtime.nefertem.specs.run.RunNefertemSpec;
import java.util.*;

/**
 * DbtValidateRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "dbt", task = "validate")
 */
public class NefertemValidateRunner implements Runner {

    private static final String TASK = "validate";
    private final String image;
    private final Map<String, Set<String>> groupedSecrets;

    public NefertemValidateRunner(String image, Map<String, Set<String>> groupedSecrets) {
        this.image = image;
        this.groupedSecrets = groupedSecrets;
    }

    @Override
    public K8sJobRunnable produce(Run runDTO) {
        // Retrieve information spec
        RunNefertemSpec runNefertemSpec = RunNefertemSpec.builder().build();
        runNefertemSpec.configure(runDTO.getSpec());

        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(runDTO.getStatus());

        List<CoreEnv> coreEnvList = new ArrayList<>(
            List.of(new CoreEnv("PROJECT_NAME", runDTO.getProject()), new CoreEnv("RUN_ID", runDTO.getId()))
        );

        Optional.ofNullable(runNefertemSpec.getTaskValidateSpec().getEnvs()).ifPresent(coreEnvList::addAll);

        //TODO: Create runnable using information from Run completed spec.
        K8sJobRunnable k8sJobRunnable = K8sJobRunnable
            .builder()
            .runtime(NefertemRuntime.RUNTIME)
            .task(TASK)
            .image(image)
            .command("python")
            .args(List.of("wrapper.py").toArray(String[]::new))
            .resources(runNefertemSpec.getTaskValidateSpec().getResources())
            .nodeSelector(runNefertemSpec.getTaskValidateSpec().getNodeSelector())
            .volumes(runNefertemSpec.getTaskValidateSpec().getVolumes())
            .secrets(groupedSecrets)
            .envs(coreEnvList)
            .labels(runNefertemSpec.getTaskValidateSpec().getLabels())
            .affinity(runNefertemSpec.getTaskValidateSpec().getAffinity())
            .tolerations(runNefertemSpec.getTaskValidateSpec().getTolerations())
            .state(statusFieldAccessor.getState())
            .build();

        k8sJobRunnable.setId(runDTO.getId());
        k8sJobRunnable.setProject(runDTO.getProject());

        return k8sJobRunnable;
    }
}
