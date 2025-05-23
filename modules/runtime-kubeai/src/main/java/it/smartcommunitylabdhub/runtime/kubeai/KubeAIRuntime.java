package it.smartcommunitylabdhub.runtime.kubeai;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.models.base.Executable;
import it.smartcommunitylabdhub.commons.models.run.Run;
import it.smartcommunitylabdhub.commons.models.task.Task;
import it.smartcommunitylabdhub.commons.models.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.services.ModelService;
import it.smartcommunitylabdhub.commons.services.SecretService;
import it.smartcommunitylabdhub.framework.k8s.base.K8sBaseRuntime;
import it.smartcommunitylabdhub.framework.k8s.kubernetes.K8sSecretHelper;
import it.smartcommunitylabdhub.framework.k8s.model.K8sServiceInfo;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sCRRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import it.smartcommunitylabdhub.runtime.kubeai.models.KubeAIAdapter;
import it.smartcommunitylabdhub.runtime.kubeai.models.KubeAIFeature;
import it.smartcommunitylabdhub.runtime.kubeai.models.OpenAIService;
import it.smartcommunitylabdhub.runtime.kubeai.specs.KubeAIServeFunctionSpec;
import it.smartcommunitylabdhub.runtime.kubeai.specs.KubeAIServeRunSpec;
import it.smartcommunitylabdhub.runtime.kubeai.specs.KubeAIServeRunStatus;
import it.smartcommunitylabdhub.runtime.kubeai.specs.KubeAIServeTaskSpec;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RuntimeComponent(runtime = KubeAIRuntime.RUNTIME)
public class KubeAIRuntime
    extends K8sBaseRuntime<KubeAIServeFunctionSpec, KubeAIServeRunSpec, KubeAIServeRunStatus, K8sCRRunnable>
    implements InitializingBean {

    public static final String RUNTIME = "kubeai";

    @Value("${runtime.kubeai.endpoint}")
    private String kubeAiEndpoint;

    @Autowired
    private ModelService modelService;

    @Autowired
    private SecretService secretService;

    @Autowired(required = false)
    private K8sSecretHelper k8sSecretHelper;

    public KubeAIRuntime() {
        super(KubeAIServeRunSpec.KIND);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // nothing to do
    }

    @Override
    public KubeAIServeRunSpec build(@NotNull Executable function, @NotNull Task task, @NotNull Run run) {
        //check run kind
        if (!KubeAIServeRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(
                        String.valueOf(run.getKind()),
                        KubeAIServeRunSpec.KIND
                    )
            );
        }

        KubeAIServeFunctionSpec funSpec = KubeAIServeFunctionSpec.with(function.getSpec());
        KubeAIServeRunSpec runSpec = KubeAIServeRunSpec.with(run.getSpec());

        String kind = task.getKind();

        //build task spec as defined
        TaskBaseSpec taskSpec =
            switch (kind) {
                case KubeAIServeTaskSpec.KIND -> {
                    yield KubeAIServeTaskSpec.with(task.getSpec());
                }
                default -> throw new IllegalArgumentException(
                    "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task."
                );
            };

        //build run merging task spec overrides
        Map<String, Serializable> map = new HashMap<>();
        map.putAll(runSpec.toMap());
        taskSpec.toMap().forEach(map::putIfAbsent);

        KubeAIServeRunSpec serveSpec = KubeAIServeRunSpec.with(map);
        //ensure function is not modified
        serveSpec.setFunctionSpec(funSpec);

        return serveSpec;
    }

    @Override
    public K8sCRRunnable run(@NotNull Run run) {
        //check run kind
        if (!KubeAIServeRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(
                        String.valueOf(run.getKind()),
                        KubeAIServeRunSpec.KIND
                    )
            );
        }

        if (k8sBuilderHelper == null || k8sSecretHelper == null) {
            throw new IllegalArgumentException("k8s helpers not available");
        }

        KubeAIServeRunSpec runSpec = KubeAIServeRunSpec.with(run.getSpec());

        // Create string run accessor from task
        RunSpecAccessor runAccessor = RunSpecAccessor.with(run.getSpec());

        return switch (runAccessor.getTask()) {
            case KubeAIServeTaskSpec.KIND -> new KubeAIServeRunner(
                runSpec.getFunctionSpec(),
                secretService.getSecretData(run.getProject(), runSpec.getSecrets()),
                k8sBuilderHelper,
                k8sSecretHelper,
                modelService
            )
                .produce(run);
            default -> throw new IllegalArgumentException("Kind not recognized. Cannot retrieve the right Runner");
        };
    }

    @Override
    public KubeAIServeRunStatus onRunning(@NotNull Run run, RunRunnable runnable) {
        KubeAIServeRunStatus status = KubeAIServeRunStatus.with(run.getStatus());

        KubeAIServeRunSpec runSpec = KubeAIServeRunSpec.with(run.getSpec());
        if (runSpec.getFunctionSpec() == null) {
            //error
            return null;
        }
        KubeAIServeFunctionSpec functionSpec = runSpec.getFunctionSpec();

        //build openapi descriptor only once
        if (status.getOpenai() == null) {
            OpenAIService openai = new OpenAIService();
            openai.setBaseUrl(kubeAiEndpoint + "/openai/v1");
            openai.setModel(functionSpec.getModelName());

            if (runnable != null && runnable instanceof K8sCRRunnable) {
                //model name is set via resource name for kubeAi
                openai.setModel(((K8sCRRunnable) runnable).getName());
            }

            if (functionSpec.getAdapters() != null) {
                openai.setAdapters(
                    functionSpec
                        .getAdapters()
                        .stream()
                        .map(a -> KubeAIAdapter.builder().name(a.getName()).build())
                        .toList()
                );
            }

            if (functionSpec.getFeatures() != null) {
                openai.setFeatures(functionSpec.getFeatures().stream().map(KubeAIFeature::name).toList());
            }

            status.setOpenai(openai);
        }

        if (status.getService() == null) {
            String baseUrl = kubeAiEndpoint + "/openai";
            K8sServiceInfo service = new K8sServiceInfo();
            service.setUrl(baseUrl);

            List<String> urls = new ArrayList<>();
            //model always available
            urls.add(baseUrl + "/v1/models");

            //feature based url
            List<KubeAIFeature> features = functionSpec.getFeatures() != null
                ? functionSpec.getFeatures()
                : Collections.emptyList();
            if (features.contains(KubeAIFeature.TextGeneration)) {
                urls.add(baseUrl + "/v1/chat/completions");
                urls.add(baseUrl + "/v1/completions");
            }
            if (features.contains(KubeAIFeature.TextEmbedding)) {
                urls.add(baseUrl + "/v1/embeddings");
            }
            if (features.contains(KubeAIFeature.SpeechToText)) {
                urls.add(baseUrl + "/v1/audio/transcriptions");
            }

            service.setUrls(urls);

            status.setService(service);
        }

        if (runnable != null && runnable instanceof K8sRunnable) {
            K8sRunnable k8sRunnable = (K8sRunnable) runnable;

            status.setState(k8sRunnable.getState());
            status.setMessage(k8sRunnable.getMessage());
        }

        if (runnable != null && runnable instanceof K8sCRRunnable) {
            //TODO fetch status to read replicas
            // K8sCRRunnable crRunnable = (K8sCRRunnable) runnable;
            // crRunnable.getStatus();

        }

        return status;
    }
}
