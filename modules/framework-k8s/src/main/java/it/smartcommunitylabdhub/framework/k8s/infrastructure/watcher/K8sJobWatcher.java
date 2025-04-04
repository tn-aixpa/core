package it.smartcommunitylabdhub.framework.k8s.infrastructure.watcher;

import io.fabric8.kubernetes.client.KubernetesClient;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sJobFramework;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor.K8sJobMonitor;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class K8sJobWatcher extends K8sBaseWatcher<K8sJobRunnable> {

    public K8sJobWatcher(KubernetesClient client, K8sJobMonitor k8sJobMonitor) {
        super(client, k8sJobMonitor);
    }

    @Override
    public void start() {
        //build core labels for jobs
        String prefix = k8sLabelHelper.getCoreLabelsNamespace();
        Map<String, String> labels = Map.of(
            "app.kubernetes.io/managed-by",
            prefix,
            prefix + "/" + "framework",
            K8sJobFramework.FRAMEWORK
        );

        //watch jobs with core labels
        log.debug("watch jobs with labels: {}", labels);
        executor.submit(() -> watchJobs(labels));

        //watch pods with core labels
        log.debug("watch pods with labels: {}", labels);
        executor.submit(() -> watchPods(labels));
    }

    private void watchJobs(Map<String, String> labels) {
        client.batch().v1().jobs().inNamespace(namespace).withLabels(labels).watch(buildWatcher());
    }

    private void watchPods(Map<String, String> labels) {
        client.pods().inNamespace(namespace).withLabels(labels).watch(buildWatcher());
    }
}
