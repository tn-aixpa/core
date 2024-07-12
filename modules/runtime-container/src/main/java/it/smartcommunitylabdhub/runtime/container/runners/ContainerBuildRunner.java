package it.smartcommunitylabdhub.runtime.container.runners;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.infrastructure.Runner;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.objects.SourceCode;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.framework.k8s.kubernetes.K8sBuilderHelper;
import it.smartcommunitylabdhub.framework.k8s.model.ContextRef;
import it.smartcommunitylabdhub.framework.k8s.model.ContextSource;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.kaniko.infrastructure.docker.DockerfileGenerator;
import it.smartcommunitylabdhub.framework.kaniko.infrastructure.docker.DockerfileGeneratorFactory;
import it.smartcommunitylabdhub.framework.kaniko.runnables.K8sKanikoRunnable;
import it.smartcommunitylabdhub.runtime.container.ContainerRuntime;
import it.smartcommunitylabdhub.runtime.container.specs.ContainerBuildTaskSpec;
import it.smartcommunitylabdhub.runtime.container.specs.ContainerFunctionSpec;
import it.smartcommunitylabdhub.runtime.container.specs.ContainerFunctionSpec.SourceCodeLanguages;
import it.smartcommunitylabdhub.runtime.container.specs.ContainerRunSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * ContainerJobRunner
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from RunnerFactory
 * you have to register it using the following annotation:
 *
 * @RunnerComponent(runtime = "container", task = "job")
 */
@Slf4j
public class ContainerBuildRunner implements Runner<K8sKanikoRunnable> {

    private static final String RUN_DEBUG =
        "PWD=`pwd`;echo \"DEBUG: dir ${PWD}\";LS=`ls -R`;echo \"DEBUG: dir content:\" && echo \"${LS}\";";

    private static final String TASK = "job";

    private final ContainerFunctionSpec functionSpec;
    private final Map<String, Set<String>> groupedSecrets;

    public ContainerBuildRunner(ContainerFunctionSpec functionContainerSpec, Map<String, Set<String>> groupedSecrets) {
        this.functionSpec = functionContainerSpec;
        this.groupedSecrets = groupedSecrets;
    }

    @Override
    public K8sKanikoRunnable produce(Run run) {
        ContainerRunSpec runSpec = new ContainerRunSpec(run.getSpec());
        ContainerBuildTaskSpec taskSpec = runSpec.getTaskBuildSpec();
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
            dockerfileGenerator.run(RUN_DEBUG);
        }

        // Add Instructions to docker file
        Optional
            .ofNullable(taskSpec.getInstructions())
            .ifPresent(instructions -> instructions.forEach(i -> dockerfileGenerator.run(i)));

        // Generate string docker file
        String dockerfile = dockerfileGenerator.build().generate();

        //read source and build context
        List<ContextRef> contextRefs = null;
        List<ContextSource> contextSources = null;

        if (functionSpec.getSource() != null && StringUtils.hasText(functionSpec.getSource().getSource())) {
            SourceCode<SourceCodeLanguages> source = functionSpec.getSource();

            try {
                //evaluate if local path (no scheme)
                UriComponents uri = UriComponentsBuilder.fromUriString(source.getSource()).build();
                String scheme = uri.getScheme();

                if (scheme != null) {
                    //write as ref
                    contextRefs = Collections.singletonList(ContextRef.from(source.getSource()));
                } else {
                    //write as source
                    String path = source.getSource();
                    if (StringUtils.hasText(source.getBase64())) {
                        contextSources =
                            Collections.singletonList(
                                (ContextSource.builder().name(path).base64(source.getBase64()).build())
                            );
                    }
                }
            } catch (IllegalArgumentException e) {
                //skip invalid source
            }
        }

        // Parse run spec
        RunSpecAccessor runSpecAccessor = RunUtils.parseTask(runSpec.getTask());

        // Build runnable
        return K8sKanikoRunnable
            .builder()
            .id(run.getId())
            .project(run.getProject())
            .runtime(ContainerRuntime.RUNTIME)
            .task(TASK)
            .state(State.READY.name())
            // Base
            .image(
                StringUtils.hasText(functionSpec.getImage())
                    ? functionSpec.getImage()
                    : K8sBuilderHelper.sanitizeNames(
                        runSpecAccessor.getProject() +
                        "-" +
                        runSpecAccessor.getFunction() +
                        ":" +
                        run.getId().substring(0, 5)
                    )
            )
            .envs(coreEnvList)
            .secrets(groupedSecrets)
            .resources(taskSpec.getResources())
            .volumes(taskSpec.getVolumes())
            .nodeSelector(taskSpec.getNodeSelector())
            .affinity(taskSpec.getAffinity())
            .tolerations(taskSpec.getTolerations())
            .runtimeClass(taskSpec.getRuntimeClass())
            .priorityClass(taskSpec.getPriorityClass())
            // Task specific
            .dockerFile(dockerfile)
            .contextRefs(contextRefs)
            .contextSources(contextSources)
            // specific
            .backoffLimit(1)
            .build();
    }
}
