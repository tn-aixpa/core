package it.smartcommunitylabdhub.runtime.python.runners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.smartcommunitylabdhub.commons.accessors.spec.TaskSpecAccessor;
import it.smartcommunitylabdhub.commons.infrastructure.Runner;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.utils.TaskUtils;
import it.smartcommunitylabdhub.framework.k8s.kubernetes.K8sBuilderHelper;
import it.smartcommunitylabdhub.framework.k8s.model.ContextRef;
import it.smartcommunitylabdhub.framework.k8s.model.ContextSource;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreLabel;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import it.smartcommunitylabdhub.runtime.python.PythonRuntime;
import it.smartcommunitylabdhub.runtime.python.model.NuclioFunctionBuilder;
import it.smartcommunitylabdhub.runtime.python.model.NuclioFunctionSpec;
import it.smartcommunitylabdhub.runtime.python.model.PythonSourceCode;
import it.smartcommunitylabdhub.runtime.python.specs.PythonFunctionSpec;
import it.smartcommunitylabdhub.runtime.python.specs.PythonJobTaskSpec;
import it.smartcommunitylabdhub.runtime.python.specs.PythonRunSpec;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class PythonJobRunner implements Runner<K8sRunnable> {

    private static ObjectMapper jsonMapper = JacksonMapper.CUSTOM_OBJECT_MAPPER;

    private final String image;
    private final String command;
    private final PythonFunctionSpec functionSpec;
    private final Map<String, Set<String>> groupedSecrets;

    private final K8sBuilderHelper k8sBuilderHelper;

    public PythonJobRunner(
        String image,
        String command,
        PythonFunctionSpec functionPythonSpec,
        Map<String, Set<String>> groupedSecrets,
        K8sBuilderHelper k8sBuilderHelper
    ) {
        this.image = image;
        this.command = command;
        this.functionSpec = functionPythonSpec;
        this.groupedSecrets = groupedSecrets;
        this.k8sBuilderHelper = k8sBuilderHelper;
    }

    @Override
    public K8sRunnable produce(Run run) {
        PythonRunSpec runSpec = new PythonRunSpec(run.getSpec());
        PythonJobTaskSpec taskSpec = runSpec.getTaskJobSpec();
        TaskSpecAccessor taskAccessor = TaskUtils.parseFunction(taskSpec.getFunction());

        try {
            List<CoreEnv> coreEnvList = new ArrayList<>(
                List.of(new CoreEnv("PROJECT_NAME", run.getProject()), new CoreEnv("RUN_ID", run.getId()))
            );

            Optional.ofNullable(taskSpec.getEnvs()).ifPresent(coreEnvList::addAll);

            //build nuclio definition
            HashMap<String, Serializable> event = new HashMap<>();

            event.put("body", jsonMapper.writeValueAsString(run));

            NuclioFunctionSpec nuclio = NuclioFunctionSpec
                .builder()
                .runtime("python")
                //invoke user code wrapped via default handler
                .handler("run_handler:handler_job")
                //directly invoke user code
                // .handler("main:" + runSpec.getFunctionSpec().getSource().getHandler())
                .event(event)
                .build();

            String nuclioFunction = NuclioFunctionBuilder.write(nuclio);

            //read source and build context
            List<ContextRef> contextRefs = null;
            List<ContextSource> contextSources = new ArrayList<>();
            ContextSource fn = ContextSource
                .builder()
                .name("function.yaml")
                .base64(Base64.getEncoder().encodeToString(nuclioFunction.getBytes(StandardCharsets.UTF_8)))
                .build();
            contextSources.add(fn);

            if (functionSpec.getSource() != null) {
                PythonSourceCode source = functionSpec.getSource();
                String path = "main.py";

                if (StringUtils.hasText(source.getSource())) {
                    try {
                        //evaluate if local path (no scheme)
                        UriComponents uri = UriComponentsBuilder.fromUriString(source.getSource()).build();
                        String scheme = uri.getScheme();

                        if (scheme != null) {
                            //write as ref
                            contextRefs = Collections.singletonList(ContextRef.from(source.getSource()));
                        } else {
                            if (StringUtils.hasText(path)) {
                                //override path for local src
                                path = uri.getPath();
                                if (path.startsWith(".")) {
                                    path = path.substring(1);
                                }
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        //skip invalid source
                    }
                }

                if (StringUtils.hasText(source.getBase64())) {
                    contextSources.add(ContextSource.builder().name(path).base64(source.getBase64()).build());
                }
            }

            //merge env with PYTHON path override
            coreEnvList.add(new CoreEnv("PYTHONPATH", "${PYTHONPATH}:/shared/"));

            K8sRunnable k8sJobRunnable = K8sJobRunnable
                .builder()
                .runtime(PythonRuntime.RUNTIME)
                .task(PythonJobTaskSpec.KIND)
                .state(State.READY.name())
                .labels(
                    k8sBuilderHelper != null
                        ? List.of(new CoreLabel(k8sBuilderHelper.getLabelName("function"), taskAccessor.getFunction()))
                        : null
                )
                //base
                .image(StringUtils.hasText(functionSpec.getImage()) ? functionSpec.getImage() : image)
                .command(command)
                // .args(new String[] { run.getProject(), run.getId() })
                .args(new String[] { "--config", "/shared/function.yaml" })
                .contextRefs(contextRefs)
                .contextSources(contextSources)
                .envs(coreEnvList)
                .secrets(groupedSecrets)
                .resources(taskSpec.getResources())
                .volumes(taskSpec.getVolumes())
                .nodeSelector(taskSpec.getNodeSelector())
                .affinity(taskSpec.getAffinity())
                .tolerations(taskSpec.getTolerations())
                .runtimeClass(taskSpec.getRuntimeClass())
                .priorityClass(taskSpec.getPriorityClass())
                //specific
                .backoffLimit(0)
                .build();

            k8sJobRunnable.setId(run.getId());
            k8sJobRunnable.setProject(run.getProject());

            return k8sJobRunnable;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
