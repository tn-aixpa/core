package it.smartcommunitylabdhub.framework.argo;

import io.argoproj.workflow.models.Workflow;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.util.ClientBuilder;
import it.smartcommunitylabdhub.commons.jackson.YamlMapperFactory;
import it.smartcommunitylabdhub.framework.argo.config.K8sArgoFrameworkConfig;
import it.smartcommunitylabdhub.framework.argo.config.TestConfiguration;
import it.smartcommunitylabdhub.framework.argo.infrastructure.k8s.K8sArgoCronWorkflowFramework;
import it.smartcommunitylabdhub.framework.argo.infrastructure.k8s.K8sArgoWorkflowFramework;
import it.smartcommunitylabdhub.framework.argo.runnables.K8sArgoCronWorkflowRunnable;
import it.smartcommunitylabdhub.framework.argo.runnables.K8sArgoWorkflowRunnable;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;

import java.util.Collections;   

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// @SpringBootTest(classes = {TestConfiguration.class, K8sArgoFrameworkConfig.class})
public class ArgoWorkflowTest {

    @Autowired
    K8sArgoWorkflowFramework framework;
    @Autowired
    K8sArgoCronWorkflowFramework cronFramework;

    // @Test
    void testArgoWorkflowK8sRaw() throws IOException, ApiException {
        ApiClient client = ClientBuilder.standard().build();
        CustomObjectsApi api = new CustomObjectsApi(client);
        String body = "apiVersion: argoproj.io/v1alpha1\n" + //
                        "kind: Workflow\n" + //
                        "metadata:\n" + //
                        "  annotations:\n" + //
                        "    workflows.argoproj.io/description: |\n" + //
                        "      This is a simple hello world example.\n" + //
                        "    workflows.argoproj.io/pod-name-format: v2\n" + //
                        "  generateName: hello-world-\n" + //
                        "  generation: 4\n" + //
                        "  labels:\n" + //
                        "    workflows.argoproj.io/archive-strategy: \"false\"\n" + //
                        "  name: hello-world-tmc7r\n" + //
                        "spec:\n" + //
                        "  arguments: {}\n" + //
                        "  entrypoint: hello-world\n" + //
                        "  templates:\n" + //
                        "  - container:\n" + //
                        "      args:\n" + //
                        "      - hello world\n" + //
                        "      command:\n" + //
                        "      - echo\n" + //
                        "      image: busybox\n" + //
                        "      name: \"\"\n" + //
                        "      resources: {}\n" + //
                        "    inputs: {}\n" + //
                        "    metadata: {}\n" + //
                        "    name: hello-world\n" + //
                        "    outputs: {}\n" + //
                        "\n" + //
                        "";
        try {
            Workflow wbody = YamlMapperFactory.yamlObjectMapper().readValue(body, Workflow.class);
            Object obody = wbody; // new ObjectMapper().writeValueAsString(wbody)
            Object result = api.createNamespacedCustomObject("argoproj.io", "v1alpha1", "argo", "workflows", obody, "true", null, null, null);
            System.out.println(result);
        } catch (ApiException e) {
            e.printStackTrace();
            System.out.println(e.getResponseBody());
        }
    }

    // @Test
    void testArgoWorkflowK8s() throws IOException, ApiException, K8sFrameworkException {
        framework.setArtifactRepositoryKey("argo-repository");
        framework.setArtifactRepositoryConfigMap("arterepo");

        String rand = java.util.UUID.randomUUID().toString().substring(0, 8);

        String body = "arguments: {}\n" + //
                        "entrypoint: hello-world\n" + //
                        "templates:\n" + //
                        "- container:\n" + //
                        "    args:\n" + //
                        "    - hello world\n" + //
                        "    command:\n" + //
                        "    - echo\n" + //
                        "    image: busybox\n" + //
                        "    name: \"\"\n" + //
                        "    resources: {}\n" + //
                        "  inputs: {}\n" + //
                        "  metadata: {}\n" + //
                        "  name: hello-world\n" + //
                        "  outputs: {}\n" + //
                        "\n" + //
                        "";


            K8sArgoWorkflowRunnable runnable = new K8sArgoWorkflowRunnable();
            runnable.setId("wf-" + rand);
            runnable.setProject("project");
            runnable.setWorkflowSpec(body);
            runnable.setRuntime("kfp");
            runnable.setEnvs(Collections.emptyList());

            framework.run(runnable);
    }

    @Test
    void testArgoCronWorkflowK8s() throws IOException, ApiException, K8sFrameworkException {
        cronFramework.setArtifactRepositoryKey("argo-repository");
        cronFramework.setArtifactRepositoryConfigMap("arterepo");

        String rand = java.util.UUID.randomUUID().toString().substring(0, 8);

        String body = "arguments: {}\n" + //
                        "entrypoint: hello-world\n" + //
                        "templates:\n" + //
                        "- container:\n" + //
                        "    args:\n" + //
                        "    - hello world\n" + //
                        "    command:\n" + //
                        "    - echo\n" + //
                        "    image: busybox\n" + //
                        "    name: \"\"\n" + //
                        "    resources: {}\n" + //
                        "  inputs: {}\n" + //
                        "  metadata: {}\n" + //
                        "  name: hello-world\n" + //
                        "  outputs: {}\n" + //
                        "\n" + //
                        "";


            K8sArgoCronWorkflowRunnable runnable = new K8sArgoCronWorkflowRunnable();
            runnable.setId("wf-" + rand);
            runnable.setProject("project");
            runnable.setWorkflowSpec(body);
            runnable.setRuntime("kfp");
            runnable.setEnvs(Collections.emptyList());
            runnable.setSchedule("*/1 * * * *");
            cronFramework.run(runnable);
    }
}
