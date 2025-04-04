package it.smartcommunitylabdhub.framework.k8s.infrastructure.watcher;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public abstract class AbstractK8sWatcherService {

    protected final ExecutorService executor = Executors.newCachedThreadPool();
    protected final KubernetesClient client;
    protected String namespace;

    // Debounce map and interval
    private final Map<String, Long> debounceMap = new ConcurrentHashMap<>();
    private static final long DEBOUNCE_INTERVAL_MS = 1000;

    public AbstractK8sWatcherService(KubernetesClient client) {
        this.client = client;
    }

    public void startWatcher(String label) {
        startPodWatcher(label);
        startSpecificWatcher(label);
    }

    private void startPodWatcher(String label) {
        executor.submit(() -> watchPods(label));
    }

    protected abstract void startSpecificWatcher(String label);

    private void watchPods(String label) {
        client
            .pods()
            .inNamespace(namespace)
            .withLabel(label)
            .watch(
                new Watcher<Pod>() {
                    @Override
                    public void eventReceived(Action action, Pod pod) {
                        String labels = pod.getMetadata().getLabels() != null
                            ? pod
                                .getMetadata()
                                .getLabels()
                                .entrySet()
                                .stream()
                                .map(e -> e.getKey() + "=" + e.getValue())
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("No Labels")
                            : "No Labels";

                        log.trace(
                            "📡 Pod Event: [{}] - Pod: [{}] - Labels: [{}]",
                            action,
                            pod.getMetadata().getName(),
                            labels
                        );
                        //TODO : do something with pod information....
                    }

                    @Override
                    public void onClose(WatcherException e) {
                        log.warn("⚠️ Pod watcher closed: {}", e.getMessage());
                    }
                }
            );
    }

    protected void debounceAndRefresh(String runnableId, Runnable refreshAction) {
        long now = System.currentTimeMillis();
        debounceMap.compute(
            runnableId,
            (key, lastExecutionTime) -> {
                if (lastExecutionTime == null || (now - lastExecutionTime) > DEBOUNCE_INTERVAL_MS) {
                    try {
                        refreshAction.run();
                    } catch (Exception e) {
                        log.error("Error refreshing: {}", e.getMessage(), e);
                    }
                    return now; // Update last execution time
                }
                return lastExecutionTime; // Keep old time if debounce is active
            }
        );
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down watchers...");
        executor.shutdownNow();
    }

    @Value("${kubernetes.namespace}")
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
