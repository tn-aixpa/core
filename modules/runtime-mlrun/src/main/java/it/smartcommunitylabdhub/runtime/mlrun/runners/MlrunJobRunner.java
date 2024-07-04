package it.smartcommunitylabdhub.runtime.mlrun.runners;

import it.smartcommunitylabdhub.commons.exceptions.CoreRuntimeException;
import it.smartcommunitylabdhub.commons.infrastructure.Runner;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.runtime.mlrun.MlrunRuntime;
import it.smartcommunitylabdhub.runtime.mlrun.specs.MlrunJobTaskSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.MlrunRunSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * MlrunMlrunRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "mlrun", task = "job")
 */
public class MlrunJobRunner implements Runner<K8sJobRunnable> {

    private static final String TASK = "job";
    private final String image;
    private final Map<String, Set<String>> groupedSecrets;

    public MlrunJobRunner(String image, Map<String, Set<String>> groupedSecrets) {
        this.image = image;
        this.groupedSecrets = groupedSecrets;
    }

    @Override
    public K8sJobRunnable produce(Run run) {
        // Retrieve information about RunMlrunSpec
        MlrunRunSpec runSpec = new MlrunRunSpec(run.getSpec());
        MlrunJobTaskSpec taskSpec = runSpec.getJobSpec();
        if (taskSpec == null) {
            throw new CoreRuntimeException("null or empty task definition");
        }

        List<CoreEnv> coreEnvList = new ArrayList<>(
            List.of(new CoreEnv("PROJECT_NAME", run.getProject()), new CoreEnv("RUN_ID", run.getId()))
        );

        Optional.ofNullable(taskSpec.getEnvs()).ifPresent(coreEnvList::addAll);

        //TODO: Create runnable using information from Run completed spec.
        K8sJobRunnable k8sJobRunnable = K8sJobRunnable
            .builder()
            .runtime(MlrunRuntime.RUNTIME)
            .task(TASK)
            .image(image)
            .command("python")
            .args(List.of("wrapper.py").toArray(String[]::new))
            .resources(taskSpec.getResources())
            .nodeSelector(taskSpec.getNodeSelector())
            .volumes(taskSpec.getVolumes())
            .secrets(groupedSecrets)
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
