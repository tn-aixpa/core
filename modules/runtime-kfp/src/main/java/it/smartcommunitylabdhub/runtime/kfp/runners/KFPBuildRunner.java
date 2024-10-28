package it.smartcommunitylabdhub.runtime.kfp.runners;

import it.smartcommunitylabdhub.commons.infrastructure.Runner;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import it.smartcommunitylabdhub.runtime.kfp.KFPRuntime;
import it.smartcommunitylabdhub.runtime.kfp.specs.KFPBuildTaskSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.KFPPipelineTaskSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.KFPRunSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * KFPBuildRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "kfp", task = "build")
 */
public class KFPBuildRunner implements Runner<K8sRunnable> {

    private final String image;
    private final Map<String, String> secretData;

    public KFPBuildRunner(String image, Map<String, String> secretData) {
        this.image = image;
        this.secretData = secretData;
    }

    @Override
    public K8sRunnable produce(Run run) {
        KFPRunSpec runSpec = new KFPRunSpec(run.getSpec());
        KFPBuildTaskSpec taskSpec = runSpec.getTaskBuildSpec();

        List<CoreEnv> coreEnvList = new ArrayList<>(
            List.of(
                new CoreEnv("PROJECT_NAME", run.getProject()),
                new CoreEnv("RUN_ID", run.getId()),
                new CoreEnv("DIGITALHUB_CORE_WORKFLOW_IMAGE", image)
            )
        );

        List<CoreEnv> coreSecrets = secretData == null
        ? null
        : secretData.entrySet().stream().map(e -> new CoreEnv(e.getKey(), e.getValue())).toList();

        Optional.ofNullable(taskSpec.getEnvs()).ifPresent(coreEnvList::addAll);

        K8sRunnable k8sJobRunnable = K8sJobRunnable
            .builder()
            .runtime(KFPRuntime.RUNTIME)
            .task(KFPPipelineTaskSpec.KIND)
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
