package it.smartcommunitylabdhub.runtime.python.runners;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.infrastructure.Runner;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.kaniko.infrastructure.docker.DockerfileGenerator;
import it.smartcommunitylabdhub.framework.kaniko.infrastructure.docker.DockerfileGeneratorFactory;
import it.smartcommunitylabdhub.framework.kaniko.runnables.K8sKanikoRunnable;
import it.smartcommunitylabdhub.runtime.python.PythonRuntime;
import it.smartcommunitylabdhub.runtime.python.specs.function.FunctionPythonSpec;
import it.smartcommunitylabdhub.runtime.python.specs.run.RunPythonSpec;
import it.smartcommunitylabdhub.runtime.python.specs.task.TaskBuildSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * PythonJobRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "container", task = "job")
 */
@Slf4j
public class PythonBuildRunner implements Runner<K8sKanikoRunnable> {

    private static final String TASK = "job";

    private final FunctionPythonSpec functionSpec;
    private final Map<String, Set<String>> groupedSecrets;

    public PythonBuildRunner(FunctionPythonSpec functionPythonSpec, Map<String, Set<String>> groupedSecrets) {
        this.functionSpec = functionPythonSpec;
        this.groupedSecrets = groupedSecrets;
    }

    @Override
    public K8sKanikoRunnable produce(Run run) {
        RunPythonSpec runSpec = new RunPythonSpec(run.getSpec());
        TaskBuildSpec taskSpec = runSpec.getTaskBuildSpec();
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(run.getStatus());

        List<CoreEnv> coreEnvList = new ArrayList<>(
            List.of(new CoreEnv("PROJECT_NAME", run.getProject()), new CoreEnv("RUN_ID", run.getId()))
        );

        Optional.ofNullable(taskSpec.getEnvs()).ifPresent(coreEnvList::addAll);

        // Generate docker file
        DockerfileGeneratorFactory dockerfileGenerator = DockerfileGenerator.factory();

        if (!StringUtils.hasText(functionSpec.getBaseImage())) {
            throw new IllegalArgumentException("invalid or missing baseImage");
        }

        // Add image to docker file
        dockerfileGenerator.from(functionSpec.getBaseImage());

        // copy context content to workdir
        dockerfileGenerator.copy(".", "./build");
        dockerfileGenerator.workdir("/build");

        if (log.isDebugEnabled()) {
            //add debug instructions to docker file
            dockerfileGenerator.run(
                "PWD=`pwd`;echo \"DEBUG: Current dir ${PWD}\";LS=`ls -R`;echo \"DEBUG: Current dir content:\" && echo \"${LS}\";"
            );
        }

        // Add Instructions to docker file
        Optional
            .ofNullable(taskSpec.getInstructions())
            .ifPresent(instructions -> instructions.forEach(i -> dockerfileGenerator.run(i)));

        // Generate string docker file
        String dockerfile = dockerfileGenerator.build().generate();

        // Parse run spec
        RunSpecAccessor runSpecAccessor = RunUtils.parseTask(runSpec.getTask());

        // Build runnable
        return K8sKanikoRunnable
            .builder()
            .id(run.getId())
            .project(run.getProject())
            .runtime(PythonRuntime.RUNTIME)
            .task(TASK)
            .state(State.READY.name())
            // Base
            .image(runSpecAccessor.getProject() + "-" + runSpecAccessor.getFunction())
            .envs(coreEnvList)
            .secrets(groupedSecrets)
            .resources(taskSpec.getResources())
            .volumes(taskSpec.getVolumes())
            .nodeSelector(taskSpec.getNodeSelector())
            .affinity(taskSpec.getAffinity())
            .tolerations(taskSpec.getTolerations())
            // Task specific
            .contextRefs(taskSpec.getContextRefs())
            .contextSources(taskSpec.getContextSources())
            .dockerFile(dockerfile)
            // specific
            .backoffLimit(1)
            .build();
    }
}
