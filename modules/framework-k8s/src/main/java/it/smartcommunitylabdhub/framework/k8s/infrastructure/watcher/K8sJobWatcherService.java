package it.smartcommunitylabdhub.framework.k8s.infrastructure.watcher;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor.K8sJobMonitor;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor.K8sServeMonitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;

@Service
@Slf4j
public class K8sJobWatcherService extends AbstractK8sWatcherService {

    private final K8sJobMonitor k8sJobMonitor;
    public K8sJobWatcherService(KubernetesClient client, K8sJobMonitor k8sJobMonitor) {
        super(client);
        this.k8sJobMonitor = k8sJobMonitor;
    }

    @Override
    protected void startSpecificWatcher(String label) {
        executor.submit(() -> watchJobs(label));
    }

    private void watchJobs(String label) {
        client.batch().v1().jobs().inNamespace(namespace).withLabel(label).watch(new Watcher<Job>() {
            @Override
            public void eventReceived(Action action, Job job) {



                // Get runnable id from job labels and refresh
                String runnableId = K8sLabelHelper.extractInstanceId(job.getMetadata().getLabels());
                debounceAndRefresh(runnableId, () -> {
                    try {
                        k8sJobMonitor.refresh(runnableId);

                        String labels = job.getMetadata().getLabels() != null
                                ? job.getMetadata().getLabels().entrySet().stream()
                                .map(e -> e.getKey() + "=" + e.getValue())
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("No Labels")
                                : "No Labels";

                        log.info("🎯 Job Event: [{}] - Job: [{}] - Labels: [{}]", action, job.getMetadata().getName(), labels);
                    } catch (StoreException e) {
                        throw new RuntimeException(e);
                    }
                });

            }

            @Override
            public void onClose(WatcherException e) {
                log.warn("⚠️ Job watcher closed: {}", e.getMessage());
            }
        });
    }
}
