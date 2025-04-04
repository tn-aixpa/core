package it.smartcommunitylabdhub.framework.k8s.infrastructure.watcher;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor.K8sServeMonitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class K8sServeWatcherService extends AbstractK8sWatcherService {

    private final K8sServeMonitor k8sServeMonitor;

    public K8sServeWatcherService(KubernetesClient client, K8sServeMonitor k8sServeMonitor) {
        super(client);
        this.k8sServeMonitor = k8sServeMonitor;
    }

    @Override
    protected void startSpecificWatcher(String label) {
        executor.submit(() -> watchServices(label));
    }

    private void watchServices(String label) {
        client
            .services()
            .inNamespace(namespace)
            .withLabel(label)
            .watch(
                new Watcher<io.fabric8.kubernetes.api.model.Service>() {
                    @Override
                    public void eventReceived(Action action, io.fabric8.kubernetes.api.model.Service service) {
                        // Get runnable id from serve labels and refresh
                        String runnableId = K8sLabelHelper.extractInstanceId(service.getMetadata().getLabels());
                        debounceAndRefresh(
                            runnableId,
                            () -> {
                                try {
                                    k8sServeMonitor.refresh(runnableId);

                                    String labels = service.getMetadata().getLabels() != null
                                        ? service
                                            .getMetadata()
                                            .getLabels()
                                            .entrySet()
                                            .stream()
                                            .map(e -> e.getKey() + "=" + e.getValue())
                                            .reduce((a, b) -> a + ", " + b)
                                            .orElse("No Labels")
                                        : "No Labels";

                                    log.info(
                                        "🌐 Service Event: [{}] - Service: [{}] - Labels: [{}]",
                                        action,
                                        service.getMetadata().getName(),
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
                        log.warn("⚠️ Service watcher closed: {}", e.getMessage());
                    }
                }
            );
    }
}
