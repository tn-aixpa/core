package it.smartcommunitylabdhub.runtime.kubeai.text;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.models.base.Executable;
import it.smartcommunitylabdhub.commons.models.run.Run;
import it.smartcommunitylabdhub.commons.models.task.Task;
import it.smartcommunitylabdhub.commons.models.task.TaskBaseSpec;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sCRRunnable;
import it.smartcommunitylabdhub.runtime.kubeai.base.KubeAIRuntime;
import it.smartcommunitylabdhub.runtime.kubeai.base.KubeAIServeRunStatus;
import it.smartcommunitylabdhub.runtime.kubeai.base.KubeAIServeRunner;
import it.smartcommunitylabdhub.runtime.kubeai.models.KubeAIEngine;
import it.smartcommunitylabdhub.runtime.kubeai.models.KubeAIFeature;
import it.smartcommunitylabdhub.runtime.kubeai.text.specs.KubeAITextFunctionSpec;
import it.smartcommunitylabdhub.runtime.kubeai.text.specs.KubeAITextRunSpec;
import it.smartcommunitylabdhub.runtime.kubeai.text.specs.KubeAITextServeTaskSpec;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RuntimeComponent(runtime = KubeAITextRuntime.RUNTIME)
public class KubeAITextRuntime extends KubeAIRuntime<KubeAITextFunctionSpec, KubeAITextRunSpec> {

    public static final String RUNTIME = "kubeai-text";

    public KubeAITextRuntime() {
        super(KubeAITextRunSpec.KIND);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // nothing to do
    }

    @Override
    public KubeAITextRunSpec build(@NotNull Executable function, @NotNull Task task, @NotNull Run run) {
        //check run kind
        if (!KubeAITextRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), KubeAITextRunSpec.KIND)
            );
        }

        KubeAITextFunctionSpec funSpec = KubeAITextFunctionSpec.with(function.getSpec());
        KubeAITextRunSpec runSpec = KubeAITextRunSpec.with(run.getSpec());

        String kind = task.getKind();

        //build task spec as defined
        TaskBaseSpec taskSpec =
            switch (kind) {
                case KubeAITextServeTaskSpec.KIND -> {
                    yield KubeAITextServeTaskSpec.with(task.getSpec());
                }
                default -> throw new IllegalArgumentException(
                    "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task."
                );
            };

        //build run merging task spec overrides
        Map<String, Serializable> map = new HashMap<>();
        map.putAll(runSpec.toMap());
        taskSpec.toMap().forEach(map::putIfAbsent);

        KubeAITextRunSpec serveSpec = KubeAITextRunSpec.with(map);

        //ensure function is not modified
        serveSpec.setFunctionSpec(funSpec);

        return serveSpec;
    }

    @Override
    public K8sCRRunnable run(@NotNull Run run) {
        //check run kind
        if (!KubeAITextRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), KubeAITextRunSpec.KIND)
            );
        }

        if (k8sBuilderHelper == null || k8sSecretHelper == null) {
            throw new IllegalArgumentException("k8s helpers not available");
        }

        KubeAITextRunSpec runSpec = KubeAITextRunSpec.with(run.getSpec());

        // Create string run accessor from task
        RunSpecAccessor runAccessor = RunSpecAccessor.with(run.getSpec());

        return switch (runAccessor.getTask()) {
            case KubeAITextServeTaskSpec.KIND -> new KubeAIServeRunner(
                KubeAITextRuntime.RUNTIME,
                runSpec.getFunctionSpec().getEngine() != null
                    ? runSpec.getFunctionSpec().getEngine().name()
                    : KubeAIEngine.VLLM.name(),
                runSpec.getFunctionSpec().getFeatures() != null
                    ? runSpec.getFunctionSpec().getFeatures().stream().map(KubeAIFeature::name).toList()
                    : Collections.emptyList(),
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
        KubeAIServeRunStatus status = super.onRunning(run, runnable);
        KubeAITextFunctionSpec functionSpec = KubeAITextFunctionSpec.with(run.getSpec());
        if (status == null || functionSpec == null) {
            return null;
        }

        if (status.getOpenai() != null) {
            //set features
            status.getOpenai().setFeatures(functionSpec.getFeatures().stream().map(KubeAIFeature::name).toList());
        }

        if (status.getService() == null) {
            //feature based urls
            String baseUrl = kubeAiEndpoint + "/openai";

            List<String> urls = status.getService() != null
                ? new ArrayList<>(status.getService().getUrls())
                : new ArrayList<>();

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

            status.getService().setUrls(urls);
        }

        return status;
    }
}
