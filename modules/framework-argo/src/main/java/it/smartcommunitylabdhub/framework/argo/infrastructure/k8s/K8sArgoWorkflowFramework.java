package it.smartcommunitylabdhub.framework.argo.infrastructure.k8s;

import com.fasterxml.jackson.core.type.TypeReference;

import io.argoproj.workflow.models.ArtifactRepositoryRef;
import io.argoproj.workflow.models.ExecutorConfig;
import io.argoproj.workflow.models.Template;
import io.argoproj.workflow.models.Workflow;
import io.argoproj.workflow.models.WorkflowSpec;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1EnvFromSource;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodSecurityContext;
import io.kubernetes.client.openapi.models.V1Secret;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.FrameworkComponent;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.jackson.YamlMapperFactory;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sBaseFramework;
import it.smartcommunitylabdhub.framework.argo.exceptions.K8sArgoFrameworkException;
import it.smartcommunitylabdhub.framework.argo.objects.K8sWorkflowObject;
import it.smartcommunitylabdhub.framework.argo.runnables.K8sArgoWorkflowRunnable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

@Slf4j
@FrameworkComponent(framework = K8sArgoWorkflowFramework.FRAMEWORK)
public class K8sArgoWorkflowFramework extends K8sBaseFramework<K8sArgoWorkflowRunnable, K8sWorkflowObject> {

    private static final String ARGO_PLURAL = "workflows";
    private static final String ARGO_VERSION = "v1alpha1";
    private static final String ARGO_GROUP = "argoproj.io";
    private static final String ARGO_API_VERSION = "argoproj.io/v1alpha1";

    public static final int DEFAULT_BACKOFF_LIMIT = 3;
    public static final String FRAMEWORK = "argoworkflow";

    private final CustomObjectsApi customObjectsApi;
    
    private String artifactRepositoryConfigMap;
    private String artifactRepositoryKey;
    private String serviceAccountName;

    private static final TypeReference<HashMap<String, Serializable>> typeRef = new TypeReference<
        HashMap<String, Serializable>
    >() {};

    public K8sArgoWorkflowFramework(ApiClient apiClient) {
        super(apiClient);
        customObjectsApi = new CustomObjectsApi(apiClient);
    }

    @Autowired
    public void setArtifactRepositoryConfigMap(@Value("${argoworkflows.artifacts.configmap}") String configmap) {
        this.artifactRepositoryConfigMap = configmap;
    }

    @Autowired
    public void setArtifactRepositoryKey(@Value("${argoworkflows.artifacts.key}") String key) {
        this.artifactRepositoryKey = key;
    }
    @Autowired
    public void setServiceAccountName(@Value("${argoworkflows.serviceaccount}") String serviceAccountName) {
        this.serviceAccountName = serviceAccountName;
    }

    @Override
    public K8sArgoWorkflowRunnable run(K8sArgoWorkflowRunnable runnable) throws K8sFrameworkException {
        log.info("run for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        Map<String, KubernetesObject> results = new HashMap<>();
        
        //secrets
        V1Secret secret = buildRunSecret(runnable);
        if (secret != null) {
            storeRunSecret(secret);
            //clear data before storing
            results.put("secret", secret.stringData(Collections.emptyMap()).data(Collections.emptyMap()));
        }

        String name = null;
        if (runnable.getWorkflowSpec() == null) {
            throw new K8sArgoFrameworkException("Missing workflow specification");                
        }

        if (runnable.getWorkflowSpec() != null) {
            //create workflow with reference to secret
            K8sWorkflowObject workflow = build(runnable);
            log.info("create workflow for {}", String.valueOf(workflow.getMetadata().getName()));
            workflow = create(workflow);
            results.put("workflow", workflow);
            name = workflow.getMetadata().getName();
        }

        //update state
        runnable.setState(State.RUNNING.name());

        //update results
        if (!"disable".equals(collectResults)) {
            //update results
            try {
                runnable.setResults(
                    results
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(Entry::getKey, e -> mapper.convertValue(e, typeRef)))
                );
            } catch (IllegalArgumentException e) {
                log.error("error reading k8s results: {}", e.getMessage());
            }
        }

        if (name != null) {
            runnable.setMessage(String.format("argo workflow %s created", name));
        }

        if (log.isTraceEnabled()) {
            log.trace("result: {}", runnable);
        }

        return runnable;
    }

    @Override
    public K8sArgoWorkflowRunnable stop(K8sArgoWorkflowRunnable runnable) throws K8sFrameworkException {
        log.info("stop for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        K8sWorkflowObject workflow = get(build(runnable));

        //stop by deleting
        log.info("stopping Argo Workflow for {}", String.valueOf(workflow.getMetadata().getName()));
        delete(workflow);

        //update state
        runnable.setState(State.STOPPED.name());

        if (log.isTraceEnabled()) {
            log.trace("result: {}", runnable);
        }

        return runnable;
    }

    @Override
    public K8sArgoWorkflowRunnable delete(K8sArgoWorkflowRunnable runnable) throws K8sFrameworkException {
        log.info("delete for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        K8sWorkflowObject workflow;
        try {
            workflow = get(build(runnable));
        } catch (K8sFrameworkException e) {
            runnable.setState(State.DELETED.name());
            return runnable;
        }

        //secrets
        cleanRunSecret(runnable);

        log.info("delete Argo Workflow for {}", String.valueOf(workflow.getMetadata().getName()));
        delete(workflow);

        //update state
        runnable.setState(State.DELETED.name());

        if (log.isTraceEnabled()) {
            log.trace("result: {}", runnable);
        }

        return runnable;
    }

    /**
     * Build Argo Workflow CR. Populate template defaults and artifact repository reference.
     * @param runnable
     * @param secret
     * @return
     * @throws K8sArgoFrameworkException
     */
    public K8sWorkflowObject build(K8sArgoWorkflowRunnable runnable) throws K8sArgoFrameworkException {
        log.debug("build for {}", runnable.getId());
        if (log.isTraceEnabled()) {
            log.trace("runnable: {}", runnable);
        }

        try {
            Workflow workflow = new Workflow();
            workflow.setApiVersion(ARGO_API_VERSION);
            workflow.setKind("Workflow");
            workflow.setMetadata(new V1ObjectMeta());
            workflow.getMetadata().setName(runnable.getId());
            WorkflowSpec workflowSpec = YamlMapperFactory.yamlObjectMapper().readValue(runnable.getWorkflowSpec(), WorkflowSpec.class);
            workflow.setSpec(workflowSpec);
            sanitize(workflow);
            //build labels
            Map<String, String> labels = buildLabels(runnable);
            // Create the Job metadata
             workflow.getMetadata().labels(labels);            
            //populate template defaults
            appendTemplateDefaults(workflow, runnable);
            //populate artifact repository
            appendArtifactRepository(workflow);
            workflow.getSpec().setSecurityContext(buildPodSecurityContext(runnable));
            return new K8sWorkflowObject(workflow);
        } catch (Exception e) {
            throw new K8sArgoFrameworkException("Error parsing the Argo workflow specification: " + e.getMessage(), e);
        }
    }

    private void sanitize(Workflow workflow) {
        // clean up envFrom from template defaults and templates
        if (workflow.getSpec().getTemplateDefaults() != null) {
            V1Container container = workflow.getSpec().getTemplateDefaults().getContainer();
            if (container != null) {
                container.setEnvFrom(Collections.emptyList());
            }            
        }
        if (workflow.getSpec().getTemplates() != null) {
            for (Template template : workflow.getSpec().getTemplates()) {
                if(template.getContainer() != null) {
                    template.getContainer().setEnvFrom(Collections.emptyList());
                }
            }
        }
    }

    private void appendArtifactRepository(Workflow workflow) {
        ArtifactRepositoryRef ref = new ArtifactRepositoryRef()
            .configMap(artifactRepositoryConfigMap)
            .key(artifactRepositoryKey);
        workflow.getSpec().setArtifactRepositoryRef(ref);
    }

    private void appendTemplateDefaults(Workflow workflow, K8sArgoWorkflowRunnable runnable) throws K8sFrameworkException {
        // Prepare environment variables for the Kubernetes job
        Template templateDefaults = workflow.getSpec().getTemplateDefaults();
        if (templateDefaults == null) {
            templateDefaults = new Template();
            workflow.getSpec().setTemplateDefaults(templateDefaults);
        }
        V1Container container = templateDefaults.getContainer();
        if (container == null) {
            container = new V1Container();
            templateDefaults.setContainer(container);
        }
        List<V1EnvFromSource> envFrom = new LinkedList<>(buildEnvFrom(runnable));
        List<V1EnvVar> env = new LinkedList<>(buildEnv(runnable));
        if (container.getEnv() != null) {
            env.addAll(container.getEnv());
        }
        if (container.getEnvFrom() != null) {
            envFrom.addAll(container.getEnvFrom());
        }

        container
            .envFrom(envFrom)
            .env(env)
            .resources(buildResources(runnable));

        templateDefaults
            .automountServiceAccountToken(false)
            .securityContext(buildPodSecurityContext(runnable))
            .container(container);

        if (templateDefaults.getExecutor() == null) {
            templateDefaults.setExecutor(new ExecutorConfig());
        }
        templateDefaults.getExecutor().setServiceAccountName(serviceAccountName);

        if (workflow.getSpec().getTemplates() != null) {
            for (Template template : workflow.getSpec().getTemplates()) {
                template
                .automountServiceAccountToken(false)
                .securityContext(templateDefaults.getSecurityContext());
            }
        }
    }

    public V1PodSecurityContext buildPodSecurityContext(K8sArgoWorkflowRunnable runnable) throws K8sFrameworkException {
        V1PodSecurityContext context = new V1PodSecurityContext();
        //enforce policy for non root when requested by admin
        if (disableRoot) {
            context.runAsNonRoot(true);
        }

        return context;
    }

    /*
     * K8s
     */
    public K8sWorkflowObject apply(@NotNull K8sWorkflowObject workflow) throws K8sFrameworkException {
        return workflow;
    }

    public K8sWorkflowObject get(@NotNull K8sWorkflowObject workflow) throws K8sFrameworkException {
        Assert.notNull(workflow.getMetadata(), "metadata can not be null");

        try {
            String wfName = workflow.getMetadata().getName();
            log.debug("get Argo Workflow for {}", wfName);

            Object object = customObjectsApi.getNamespacedCustomObject(
                    ARGO_GROUP, ARGO_VERSION, namespace, ARGO_PLURAL, wfName);
            Workflow readWorkflow = JacksonMapper.OBJECT_MAPPER.convertValue(object, Workflow.class);
            return new K8sWorkflowObject(readWorkflow);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getMessage(), e.getResponseBody());
        }
    }

    public K8sWorkflowObject create(K8sWorkflowObject workflow) throws K8sFrameworkException {
        Assert.notNull(workflow.getMetadata(), "metadata can not be null");

        try {
            String workflowName = workflow.getMetadata().getName();
            log.debug("create Argo Workflow for {}", workflowName);

            //dispatch job via api
            Object created = customObjectsApi.createNamespacedCustomObject(
                    ARGO_GROUP, ARGO_VERSION, namespace, ARGO_PLURAL, workflow.getWorkflow(),
                    "true", null, null, null);
            Workflow createdWorkflow = JacksonMapper.OBJECT_MAPPER.convertValue(created, Workflow.class);
            return new K8sWorkflowObject(createdWorkflow);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getResponseBody());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getMessage(), e.getResponseBody());
        }
    }

    public void delete(K8sWorkflowObject workflow) throws K8sFrameworkException {
        Assert.notNull(workflow.getMetadata(), "metadata can not be null");

        try {
            String wfName = workflow.getMetadata().getName();
            log.debug("delete Argo Workflow for {}", wfName);
            customObjectsApi.deleteNamespacedCustomObject(ARGO_GROUP, ARGO_VERSION, namespace, ARGO_PLURAL, wfName, null, null, null, null, null);
        } catch (ApiException e) {
            log.error("Error with k8s: {}", e.getResponseBody());
            if (log.isDebugEnabled()) {
                log.debug("k8s api response: {}", e.getResponseBody());
            }

            throw new K8sFrameworkException(e.getMessage(), e.getResponseBody());
        }
    }

}
