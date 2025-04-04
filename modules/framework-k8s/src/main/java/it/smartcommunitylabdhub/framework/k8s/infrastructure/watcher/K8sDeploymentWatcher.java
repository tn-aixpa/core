package it.smartcommunitylabdhub.framework.k8s.infrastructure.watcher;

import io.fabric8.kubernetes.client.KubernetesClient;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sDeploymentFramework;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor.K8sDeploymentMonitor;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sDeploymentRunnable;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class K8sDeploymentWatcher extends K8sBaseWatcher<K8sDeploymentRunnable> {

    public K8sDeploymentWatcher(KubernetesClient client, K8sDeploymentMonitor k8sDeploymentMonitor) {
        super(client, k8sDeploymentMonitor);
    }

    @Override
    public void start() {
        //build core labels for deployment
        String prefix = k8sLabelHelper.getCoreLabelsNamespace();
        Map<String, String> labels = Map.of(
            "app.kubernetes.io/managed-by",
            prefix,
            prefix + "/" + "framework",
            K8sDeploymentFramework.FRAMEWORK
        );

        //watch deployments with core labels
        log.debug("watch deployments with labels: {}", labels);
        executor.submit(() -> watchDeployments(labels));

        //watch pods with core labels
        log.debug("watch pods with labels: {}", labels);
        executor.submit(() -> watchPods(labels));
    }

    private void watchDeployments(Map<String, String> labels) {
        client.apps().deployments().inNamespace(namespace).withLabels(labels).watch(buildWatcher());
    }

    private void watchPods(Map<String, String> labels) {
        client.pods().inNamespace(namespace).withLabels(labels).watch(buildWatcher());
    }
}
