/**
 * PollingService.java
 * <p>
 * This class provides a convenient interface to manage multiple Pollers.
 * It allows creating, starting, stopping, and removing Pollers easily.
 */

package it.smartcommunitylabdhub.core.components.pollers;

import it.smartcommunitylabdhub.core.components.workflows.factory.Workflow;
import org.springframework.core.task.TaskExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollingService {
    private final Map<String, Poller> pollerMap;
    private final TaskExecutor executor;

    public PollingService(TaskExecutor executor) {
        this.pollerMap = new HashMap<>();
        this.executor = executor;
    }

    public void createPoller(String name, List<Workflow> workflowList, long delay, boolean reschedule, boolean asyncWorkflow) {
        Poller poller = new Poller(name, workflowList, delay, reschedule, asyncWorkflow, executor);
        pollerMap.put(name, poller);
    }

    public void startPolling() {
        pollerMap.forEach((key, value) -> value.startPolling());
    }

    public void stopPolling() {
        pollerMap.forEach((key, value) -> value.stopPolling());
        pollerMap.clear();
    }

    public void startOne(String name) {
        pollerMap.get(name).startPolling();
    }

    public void stopOne(String name) {
        pollerMap.get(name).stopPolling();
        pollerMap.remove(name);
    }

    public void remove(String name) {
        pollerMap.remove(name);
    }
}