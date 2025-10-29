/*
 * SPDX-FileCopyrightText: © 2025 DSLab - Fondazione Bruno Kessler
 *
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package it.smartcommunitylabdhub.runtime.huggingface;

import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.accessors.fields.KeyAccessor;
import it.smartcommunitylabdhub.commons.accessors.spec.TaskSpecAccessor;
import it.smartcommunitylabdhub.commons.exceptions.CoreRuntimeException;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.function.Function;
import it.smartcommunitylabdhub.commons.models.model.Model;
import it.smartcommunitylabdhub.commons.models.run.Run;
import it.smartcommunitylabdhub.commons.services.FunctionManager;
import it.smartcommunitylabdhub.commons.services.ModelManager;
import it.smartcommunitylabdhub.framework.k8s.kubernetes.K8sBuilderHelper;
import it.smartcommunitylabdhub.framework.k8s.model.ContextRef;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreLabel;
import it.smartcommunitylabdhub.framework.k8s.objects.CorePort;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sServeRunnable;
import it.smartcommunitylabdhub.relationships.RelationshipDetail;
import it.smartcommunitylabdhub.relationships.RelationshipName;
import it.smartcommunitylabdhub.relationships.RelationshipsMetadata;
import it.smartcommunitylabdhub.runtime.huggingface.specs.HuggingfaceServeFunctionSpec;
import it.smartcommunitylabdhub.runtime.huggingface.specs.HuggingfaceServeRunSpec;
import it.smartcommunitylabdhub.runtime.huggingface.specs.HuggingfaceServeTaskSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class HuggingfaceServeRunner {

    private static final int HTTP_PORT = 8080;
    private static final int GRPC_PORT = 8081;
    private static final int UID = 1000;
    private static final int GID = 1000;

    private final String image;
    private final int userId;
    private final int groupId;
    private final HuggingfaceServeFunctionSpec functionSpec;
    private final Map<String, String> secretData;

    private final K8sBuilderHelper k8sBuilderHelper;
    private final ModelManager modelService;
    private final FunctionManager functionService;

    public HuggingfaceServeRunner(
        String image,
        Integer userId,
        Integer groupId,
        HuggingfaceServeFunctionSpec functionSpec,
        Map<String, String> secretData,
        K8sBuilderHelper k8sBuilderHelper,
        ModelManager modelService,
        FunctionManager functionService
    ) {
        this.image = image;
        this.functionSpec = functionSpec;
        this.secretData = secretData;
        this.k8sBuilderHelper = k8sBuilderHelper;
        this.modelService = modelService;
        this.functionService = functionService;

        this.userId = userId != null ? userId : UID;
        this.groupId = groupId != null ? groupId : GID;
    }

    public K8sRunnable produce(Run run) {
        HuggingfaceServeRunSpec runSpec = HuggingfaceServeRunSpec.with(run.getSpec());
        HuggingfaceServeTaskSpec taskSpec = runSpec.getTaskServeSpec();
        TaskSpecAccessor taskAccessor = TaskSpecAccessor.with(taskSpec.toMap());

        List<CoreEnv> coreEnvList = new ArrayList<>(
            List.of(new CoreEnv("PROJECT_NAME", run.getProject()), new CoreEnv("RUN_ID", run.getId()))
        );

        List<CoreEnv> coreSecrets = secretData == null
            ? null
            : secretData.entrySet().stream().map(e -> new CoreEnv(e.getKey(), e.getValue())).toList();

        Optional.ofNullable(taskSpec.getEnvs()).ifPresent(coreEnvList::addAll);

        //read source and build context
        List<ContextRef> contextRefs = null;
        String path = functionSpec.getPath();
        if (path.startsWith(Keys.STORE_PREFIX)) {
            KeyAccessor keyAccessor = KeyAccessor.with(path);
            if (!EntityName.MODEL.getValue().equals(keyAccessor.getType())) {
                throw new CoreRuntimeException("invalid entity kind reference, expected model");
            }
            Model model = keyAccessor.getId() != null
                ? modelService.findModel(keyAccessor.getId())
                : modelService.getLatestModel(keyAccessor.getProject(), keyAccessor.getName());
            if (model == null) {
                throw new CoreRuntimeException("invalid entity reference, HuggingFace model not found");
            }
            if (!model.getKind().equals("huggingface") && !model.getKind().equals("hf")) {
                throw new CoreRuntimeException("invalid entity reference, expected HuggingFace model");
            }
            RelationshipDetail rel = new RelationshipDetail();
            rel.setType(RelationshipName.CONSUMES);
            rel.setDest(run.getKey());
            rel.setSource(model.getKey());
            RelationshipsMetadata relationships = RelationshipsMetadata.from(run.getMetadata());
            relationships.getRelationships().add(rel);
            run.getMetadata().putAll(relationships.toMap());
            path = (String) model.getSpec().get("path");
        }

        UriComponents uri = UriComponentsBuilder.fromUriString(path).build();

        List<String> args = new ArrayList<>(
            List.of(
                "-m",
                "huggingfaceserver",
                "--model_name",
                StringUtils.hasText(functionSpec.getModelName()) ? functionSpec.getModelName() : "model",
                "--protocol",
                "v2",
                "--enable_docs_url",
                "true"
            )
        );

        // model dir or model id
        if ("huggingface".equals(uri.getScheme()) || "hf".equals(uri.getScheme())) {
            String mdlId = uri.getHost() + uri.getPath();
            String revision = null;
            if (mdlId.contains(":")) {
                String[] parts = mdlId.split(":");
                mdlId = parts[0];
                revision = parts[1];
            }
            args.add("--model_id");
            args.add(mdlId);
            if (revision != null) {
                args.add("--model_revision");
                args.add(revision);
            }
        } else {
            args.add("--model_dir");
            args.add("/shared/model");

            contextRefs =
                Collections.singletonList(
                    ContextRef.builder().source(path).protocol(uri.getScheme()).destination("model").build()
                );
        }

        Map<String, String> extraArgMap = new HashMap<>();

        // tokenizer revision
        if (StringUtils.hasText(taskSpec.getTokenizerRevision())) {
            extraArgMap.put("--tokenizer_revision", taskSpec.getTokenizerRevision());
        }
        // max length
        if (taskSpec.getMaxLength() != null) {
            extraArgMap.put("--max_length", taskSpec.getMaxLength().toString());
        }
        // disable_lower_case
        if (taskSpec.getDisableLowerCase() != null) {
            extraArgMap.put("--disable_lower_case", taskSpec.getDisableLowerCase().toString());
        }
        // disable_special_tokens
        if (taskSpec.getDisableSpecialTokens() != null) {
            extraArgMap.put("--disable_special_tokens", taskSpec.getDisableSpecialTokens().toString());
        }
        // trust_remote_code
        if (taskSpec.getTrustRemoteCode() != null) {
            extraArgMap.put("--trust_remote_code", taskSpec.getTrustRemoteCode().toString());
        } else {
            extraArgMap.put("--trust_remote_code", "true");
        }
        // tensor_input_names
        if (taskSpec.getTensorInputNames() != null) {
            extraArgMap.put("--tensor_input_names", StringUtils.collectionToCommaDelimitedString(taskSpec.getTensorInputNames()));
        }
        // task
        if (taskSpec.getHuggingfaceTask() != null) {
            extraArgMap.put("--task", taskSpec.getHuggingfaceTask().getTask());
        }
        // backend
        if (taskSpec.getBackend() != null) {
            extraArgMap.put("--backend", taskSpec.getBackend().getBackend());
        }
        // return_token_type_ids
        if (taskSpec.getReturnTokenTypeIds() != null) {
            extraArgMap.put("--return_token_type_ids", taskSpec.getReturnTokenTypeIds().toString());
        }
        // return_probabilities
        if (taskSpec.getReturnProbabilities() != null) {
            extraArgMap.put("--return_probabilities", taskSpec.getReturnProbabilities().toString());
        }
        // disable_log_requests
        if (taskSpec.getDisableLogRequests() != null) {
            extraArgMap.put("--disable_log_requests", taskSpec.getDisableLogRequests().toString());
        }
        // max_log_len
        if (taskSpec.getMaxLogLen() != null) {
            extraArgMap.put("--max_log_len", taskSpec.getMaxLogLen().toString());
        }
        // dtype
        if (taskSpec.getDtype() != null) {
            extraArgMap.put("--dtype", taskSpec.getDtype().getDType());
        }

        if (runSpec.getArgs() != null && runSpec.getArgs().size() > 0) {
            mergeArgs(extraArgMap, args);
        }

        for (Map.Entry<String, String> arg : extraArgMap.entrySet()) {
            args.add(arg.getKey());
            args.add(arg.getValue());
        }


        // if (functionSpec.getAdapters() != null && functionSpec.getAdapters().size() > 0) {
        //     contextRefs = new LinkedList<>(contextRefs);
        //     args.add("--enable-lora");
        //     args.add("--lora-modules");

        //     for (Map.Entry<String, String> adapter : functionSpec.getAdapters().entrySet()) {
        //         UriComponents adapterUri = UriComponentsBuilder.fromUriString(adapter.getValue()).build();
        //         String adapterSource = adapter.getValue().trim();
        //         String ref = adapterSource;

        //         if (!"huggingface".equals(adapterUri.getScheme())) {
        //             if (!adapterSource.endsWith("/")) adapterSource += "/";
        //             ref = "/shared/adapters/" + adapter.getKey() + "/";
        //             contextRefs =
        //                 Collections.singletonList(
        //                     ContextRef
        //                         .builder()
        //                         .source(adapterSource)
        //                         .protocol(adapterUri.getScheme())
        //                         .destination("adapters/" + adapter.getKey())
        //                         .build()
        //                 );
        //         }
        //         args.add(adapter.getKey() +  "=" + ref);
        //     }
        // }

        CorePort servicePort = new CorePort(HTTP_PORT, HTTP_PORT);
        CorePort grpcPort = new CorePort(GRPC_PORT, GRPC_PORT);

        //evaluate service names
        List<String> serviceNames = new ArrayList<>();
        if (taskSpec.getServiceName() != null && StringUtils.hasText(taskSpec.getServiceName())) {
            //prepend with function name
            serviceNames.add(taskAccessor.getFunction() + "-" + taskSpec.getServiceName());
        }

        if (functionService != null) {
            //check if latest
            Function latest = functionService.getLatestFunction(run.getProject(), taskAccessor.getFunction());
            if (taskAccessor.getFunctionId().equals(latest.getId())) {
                //prepend with function name
                serviceNames.add(taskAccessor.getFunction() + "-latest");
            }
        }

        String img = StringUtils.hasText(functionSpec.getImage()) ? functionSpec.getImage() : image;

        //validate image
        if (img == null || !img.startsWith(HuggingfaceServeRuntime.IMAGE)) {
            throw new IllegalArgumentException(
                "invalid or empty image, must be based on " + HuggingfaceServeRuntime.IMAGE
            );
        }

        //build runnable
        K8sRunnable k8sServeRunnable = K8sServeRunnable
            .builder()
            .runtime(HuggingfaceServeRuntime.RUNTIME)
            .task(HuggingfaceServeTaskSpec.KIND)
            .state(State.READY.name())
            .labels(
                k8sBuilderHelper != null
                    ? List.of(new CoreLabel(k8sBuilderHelper.getLabelName("function"), taskAccessor.getFunction()))
                    : null
            )
            //base
            .image(img)
            .command("python")
            .args(args.toArray(new String[0]))
            .contextRefs(contextRefs)
            .envs(coreEnvList)
            .secrets(coreSecrets)
            .resources(k8sBuilderHelper != null ? k8sBuilderHelper.convertResources(taskSpec.getResources()) : null)
            .volumes(taskSpec.getVolumes())
            .nodeSelector(taskSpec.getNodeSelector())
            .affinity(taskSpec.getAffinity())
            .tolerations(taskSpec.getTolerations())
            .runtimeClass(taskSpec.getRuntimeClass())
            .priorityClass(taskSpec.getPriorityClass())
            .template(taskSpec.getProfile())
            //specific
            .replicas(taskSpec.getReplicas())
            .servicePorts(List.of(servicePort, grpcPort))
            .serviceType(taskSpec.getServiceType())
            .serviceNames(serviceNames != null && !serviceNames.isEmpty() ? serviceNames : null)
            //fixed securityContext
            .fsGroup(groupId)
            .runAsGroup(groupId)
            .runAsUser(userId)
            .build();

        k8sServeRunnable.setId(run.getId());
        k8sServeRunnable.setProject(run.getProject());

        return k8sServeRunnable;
    }

    private void mergeArgs(Map<String, String> extraArgMap, List<String> explicitArgs) {
        // merge explicit args into args, if not exists

        // assume key value sequence
        for (int  i = 0; i < explicitArgs.size(); i += 2) {
            if (!extraArgMap.containsKey(explicitArgs.get(i))) {
                extraArgMap.put(explicitArgs.get(i), explicitArgs.get(i + 1));
            }

        }
    }
}
