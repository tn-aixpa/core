package it.smartcommunitylabdhub.framework.k8s.infrastructure.watcher;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor.K8sDeploymentMonitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class K8sDeploymentWatcherService extends AbstractK8sWatcherService {

    private final K8sDeploymentMonitor k8sDeploymentMonitor;

    public K8sDeploymentWatcherService(KubernetesClient client, K8sDeploymentMonitor k8sDeploymentMonitor) {
        super(client);
        this.k8sDeploymentMonitor = k8sDeploymentMonitor;
    }

    @Override
    protected void startSpecificWatcher(String label) {
        executor.submit(() -> watchDeployments(label));
    }

    private void watchDeployments(String label) {
        client
            .apps()
            .deployments()
            .inNamespace(namespace)
            .withLabel(label)
            .watch(
                new Watcher<Deployment>() {
                    @Override
                    public void eventReceived(Action action, Deployment deployment) {
                        // Get runnable id from deployment labels and refresh
                        String runnableId = K8sLabelHelper.extractInstanceId(deployment.getMetadata().getLabels());
                        debounceAndRefresh(
                            runnableId,
                            () -> {
                                try {
                                    k8sDeploymentMonitor.refresh(runnableId);

                                    String labels = deployment.getMetadata().getLabels() != null
                                        ? deployment
                                            .getMetadata()
                                            .getLabels()
                                            .entrySet()
                                            .stream()
                                            .map(e -> e.getKey() + "=" + e.getValue())
                                            .reduce((a, b) -> a + ", " + b)
                                            .orElse("No Labels")
                                        : "No Labels";

                                    log.info(
                                        "🚀 Deployment Event: [{}] - Deployment: [{}] - Labels: [{}]",
                                        action,
                                        deployment.getMetadata().getName(),
                                        labels
                                    );
                                } catch (StoreException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        );
                    }

                    @Override
                    public void onClose(WatcherException e) {
                        log.warn("⚠️ Deployment watcher closed: {}", e.getMessage());
                    }
                }
            );
    }
}
