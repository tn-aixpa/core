package it.smartcommunitylabdhub.runtime.container.runners;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.infrastructure.Runner;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import it.smartcommunitylabdhub.framework.kaniko.runnables.K8sKanikoRunnable;
import it.smartcommunitylabdhub.runtime.container.ContainerRuntime;
import it.smartcommunitylabdhub.runtime.container.docker.DockerfileGenerator;
import it.smartcommunitylabdhub.runtime.container.docker.DockerfileInstruction;
import it.smartcommunitylabdhub.runtime.container.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.runtime.container.specs.run.RunContainerSpec;
import it.smartcommunitylabdhub.runtime.container.specs.task.TaskBuildSpec;
import java.util.*;

/**
 * ContainerJobRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "container", task = "job")
 */
public class ContainerBuildRunner implements Runner<K8sRunnable> {

    private static final String TASK = "job";

    private final FunctionContainerSpec functionSpec;
    private final Map<String, Set<String>> groupedSecrets;

    public ContainerBuildRunner(FunctionContainerSpec functionContainerSpec, Map<String, Set<String>> groupedSecrets) {
        this.functionSpec = functionContainerSpec;
        this.groupedSecrets = groupedSecrets;
    }

    @Override
    public K8sRunnable produce(Run run) {
        RunContainerSpec runSpec = new RunContainerSpec(run.getSpec());
        TaskBuildSpec taskSpec = runSpec.getTaskBuildSpec();
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(run.getStatus());

        List<CoreEnv> coreEnvList = new ArrayList<>(
                List.of(
                        new CoreEnv("PROJECT_NAME", run.getProject()),
                        new CoreEnv("RUN_ID", run.getId())
                )
        );

        Optional.ofNullable(taskSpec.getEnvs()).ifPresent(coreEnvList::addAll);

        // Generate docker file
        DockerfileGenerator dockerfileGenerator = new DockerfileGenerator();

        // Add image to docker file
        dockerfileGenerator.addInstruction(DockerfileInstruction.FROM, "FROM " + functionSpec.getImage());

        // Add Instructions to docker file
        Optional.ofNullable(taskSpec.getInstructions()).ifPresent(instructions ->
                instructions.forEach(instruction -> dockerfileGenerator
                        .addInstruction(DockerfileInstruction.RUN, "RUN " + instruction))
        );

        // Generate string docker file
        String dockerfile = dockerfileGenerator.generateDockerfile();

        RunSpecAccessor runSpecAccessor = RunUtils.parseTask(runSpec.getTask());


        K8sRunnable k8sKanikoRunnable = K8sKanikoRunnable
                .builder()
                .id(run.getId())
                .project(run.getProject())
                .runtime(ContainerRuntime.RUNTIME)
                .task(TASK)
                .state(State.READY.name())
                //base
                .image(runSpecAccessor.getProject() + "-" + runSpecAccessor.getFunction())
                .envs(coreEnvList)
                .secrets(groupedSecrets)
                .resources(taskSpec.getResources())
                .volumes(taskSpec.getVolumes())
                .nodeSelector(taskSpec.getNodeSelector())
                .affinity(taskSpec.getAffinity())
                .tolerations(taskSpec.getTolerations())
                .labels(taskSpec.getLabels())

                //kaniko
                .contextRefs(taskSpec.getContextRefs())
                .contextSources(taskSpec.getContextSources())
                .dockerFile(dockerfile)

                //specific
                .backoffLimit(1)
                .build();


        return k8sKanikoRunnable;
    }
}
