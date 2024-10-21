package it.smartcommunitylabdhub.runtime.dbt.runners;

import it.smartcommunitylabdhub.commons.exceptions.CoreRuntimeException;
import it.smartcommunitylabdhub.commons.infrastructure.Runner;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.runtime.dbt.DbtRuntime;
import it.smartcommunitylabdhub.runtime.dbt.specs.DbtRunSpec;
import it.smartcommunitylabdhub.runtime.dbt.specs.DbtTransformSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DbtTransformRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "dbt", task = "transform")
 */
public class DbtTransformRunner implements Runner<K8sJobRunnable> {

    private final String image;
    private final Map<String, String> secretData;

    public DbtTransformRunner(String image, Map<String, String> secretData) {
        this.image = image;
        this.secretData = secretData;
    }

    @Override
    public K8sJobRunnable produce(Run run) {
        // Retrieve information about RunDbtSpec
        DbtRunSpec runSpec = new DbtRunSpec(run.getSpec());
        DbtTransformSpec taskSpec = runSpec.getTaskSpec();
        if (taskSpec == null) {
            throw new CoreRuntimeException("null or empty task definition");
        }

        List<CoreEnv> coreEnvList = new ArrayList<>(
            List.of(new CoreEnv("PROJECT_NAME", run.getProject()), new CoreEnv("RUN_ID", run.getId()))
        );
        List<CoreEnv> coreSecrets = secretData == null
            ? null
            : secretData.entrySet().stream().map(e -> new CoreEnv(e.getKey(), e.getValue())).toList();

        Optional.ofNullable(taskSpec.getEnvs()).ifPresent(coreEnvList::addAll);

        //TODO: Create runnable using information from Run completed spec.
        K8sJobRunnable k8sJobRunnable = K8sJobRunnable
            .builder()
            .runtime(DbtRuntime.RUNTIME)
            .task(DbtTransformSpec.KIND)
            .image(image)
            .command("python")
            .args(List.of("wrapper.py").toArray(String[]::new))
            .resources(taskSpec.getResources())
            .nodeSelector(taskSpec.getNodeSelector())
            .volumes(taskSpec.getVolumes())
            .secrets(coreSecrets)
            .envs(coreEnvList)
            .affinity(taskSpec.getAffinity())
            .tolerations(taskSpec.getTolerations())
            .state(State.READY.name())
            .build();

        k8sJobRunnable.setId(run.getId());
        k8sJobRunnable.setProject(run.getProject());

        return k8sJobRunnable;
    }
}
