package it.smartcommunitylabdhub.runtime.python.runners;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.infrastructure.Runner;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sCronJobRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import it.smartcommunitylabdhub.runtime.python.PythonRuntime;
import it.smartcommunitylabdhub.runtime.python.specs.function.FunctionPythonSpec;
import it.smartcommunitylabdhub.runtime.python.specs.run.RunPythonSpec;
import it.smartcommunitylabdhub.runtime.python.specs.task.TaskJobSpec;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * ContainerJobRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "container", task = "job")
 */
public class PythonJobRunner implements Runner<K8sRunnable> {

    private static final String TASK = "job";

    private final FunctionPythonSpec functionSpec;
    private final Map<String, Set<String>> groupedSecrets;

    public PythonJobRunner(FunctionPythonSpec functionPythonSpec, Map<String, Set<String>> groupedSecrets) {
        this.functionSpec = functionPythonSpec;
        this.groupedSecrets = groupedSecrets;
    }

    @Override
    public K8sRunnable produce(Run run) {
        RunPythonSpec runSpec = new RunPythonSpec(run.getSpec());
        TaskJobSpec taskSpec = runSpec.getTaskJobSpec();
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(run.getStatus());

        List<CoreEnv> coreEnvList = new ArrayList<>(
            List.of(new CoreEnv("PROJECT_NAME", run.getProject()), new CoreEnv("RUN_ID", run.getId()))
        );

        Optional.ofNullable(taskSpec.getEnvs()).ifPresent(coreEnvList::addAll);

        K8sRunnable k8sJobRunnable = K8sJobRunnable
            .builder()
            .runtime(PythonRuntime.RUNTIME)
            .task(TASK)
            .state(State.READY.name())
            //base
            .image(functionSpec.getImage())
            .command(functionSpec.getCommand())
            .args(functionSpec.getRequirements() != null ? functionSpec.getRequirements().toArray(new String[0]) : null)
            .envs(coreEnvList)
            .secrets(groupedSecrets)
            .resources(taskSpec.getResources())
            .volumes(taskSpec.getVolumes())
            .nodeSelector(taskSpec.getNodeSelector())
            .affinity(taskSpec.getAffinity())
            .tolerations(taskSpec.getTolerations())
            //specific
            .backoffLimit(taskSpec.getBackoffLimit())
            .build();

        if (StringUtils.hasText(taskSpec.getSchedule())) {
            //build a cronJob
            k8sJobRunnable =
                K8sCronJobRunnable
                    .builder()
                    .runtime(PythonRuntime.RUNTIME)
                    .task(TASK)
                    .state(State.READY.name())
                    //base
                    .image(functionSpec.getImage())
                    .command(functionSpec.getCommand())
                    .args(functionSpec.getRequirements() != null ? functionSpec.getRequirements().toArray(new String[0]) : null)
                    .envs(coreEnvList)
                    .secrets(groupedSecrets)
                    .resources(taskSpec.getResources())
                    .volumes(taskSpec.getVolumes())
                    .nodeSelector(taskSpec.getNodeSelector())
                    .affinity(taskSpec.getAffinity())
                    .tolerations(taskSpec.getTolerations())
                    //specific
                    .backoffLimit(taskSpec.getBackoffLimit())
                    .schedule(taskSpec.getSchedule())
                    .build();
        }

        k8sJobRunnable.setId(run.getId());
        k8sJobRunnable.setProject(run.getProject());

        return k8sJobRunnable;
    }
}
