package it.smartcommunitylabdhub.runtime.kfp.runners;

import it.smartcommunitylabdhub.commons.infrastructure.Runner;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sCronJobRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import it.smartcommunitylabdhub.runtime.kfp.KFPRuntime;
import it.smartcommunitylabdhub.runtime.kfp.specs.KFPPipelineTaskSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.KFPRunSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.KFPWorkflowSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.util.StringUtils;

/**
 * KFPPipelineRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "kfp", task = "pipeline")
 */
public class KFPPipelineRunner implements Runner<K8sRunnable> {

    private final String image;
    private final Map<String, Set<String>> groupedSecrets;

    public KFPPipelineRunner(String image, Map<String, Set<String>> groupedSecrets) {
        this.image = image;
        this.groupedSecrets = groupedSecrets;
    }

    @Override
    public K8sRunnable produce(Run run) {
        KFPRunSpec runSpec = new KFPRunSpec(run.getSpec());
        KFPPipelineTaskSpec taskSpec = runSpec.getTaskPipelineSpec();

        List<CoreEnv> coreEnvList = new ArrayList<>(
            List.of(
                new CoreEnv("PROJECT_NAME", run.getProject()),
                new CoreEnv("RUN_ID", run.getId()),
                new CoreEnv("DIGITALHUB_CORE_WORKFLOW_IMAGE", image)
            )
        );

        Optional.ofNullable(taskSpec.getEnvs()).ifPresent(coreEnvList::addAll);

        //validate workflow definition
        KFPWorkflowSpec workflowSpec = runSpec.getWorkflowSpec();
        if (!StringUtils.hasText(workflowSpec.getWorkflow())) {
            //not executable
            throw new IllegalArgumentException("missing or invalid workflow definition");
        }

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
            .secrets(groupedSecrets)
            .envs(coreEnvList)
            .affinity(taskSpec.getAffinity())
            .tolerations(taskSpec.getTolerations())
            .state(State.READY.name())
            .build();

        if (StringUtils.hasText(taskSpec.getSchedule())) {
            //build a cronJob
            k8sJobRunnable =
                K8sCronJobRunnable
                    .builder()
                    .runtime(KFPRuntime.RUNTIME)
                    .task(KFPPipelineTaskSpec.KIND)
                    //base
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
                    .schedule(taskSpec.getSchedule())
                    .build();
        }

        k8sJobRunnable.setId(run.getId());
        k8sJobRunnable.setProject(run.getProject());

        return k8sJobRunnable;
    }
}
