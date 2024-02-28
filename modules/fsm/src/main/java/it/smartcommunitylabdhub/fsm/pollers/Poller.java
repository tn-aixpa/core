package it.smartcommunitylabdhub.fsm.pollers;

import it.smartcommunitylabdhub.fsm.exceptions.StopPoller;
import it.smartcommunitylabdhub.fsm.workflow.Workflow;
import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;

/**
 * The Poller class is responsible for executing a list of workflows at scheduled intervals.
 * It provides support for both synchronous and asynchronous execution of workflows.
 */
@Slf4j
public class Poller implements Runnable {

    // List of workflows to be executed by the poller
    private final Workflow workflow;

    // Scheduler for scheduling and executing tasks
    private final ScheduledExecutorService scheduledExecutorService;

    // Delay between consecutive polling runs
    private final long delay;

    // Flag to determine whether to reschedule after each run
    private final boolean reschedule;

    // Name of the poller
    private final String name;

    // Flag to indicate whether workflows should be executed asynchronously
    private final Boolean workflowsAsync;

    // Flag indicating the poller's active state
    private boolean active;

    /**
     * Constructs a Poller with the specified parameters.
     *
     * @param name           The name of the poller.
     * @param workflow       The workflow to be executed.
     * @param delay          Delay between consecutive polling runs in seconds.
     * @param reschedule     Flag indicating whether to reschedule after each run.
     * @param workflowsAsync Flag indicating whether workflows should be executed asynchronously.
     * @param executor       Task executor for handling asynchronous workflow execution.
     */
    public Poller(
        String name,
        Workflow workflow,
        long delay,
        boolean reschedule,
        boolean workflowsAsync,
        TaskExecutor executor
    ) {
        this.name = name;
        this.workflow = workflow;
        this.delay = delay;
        this.reschedule = reschedule;
        this.active = true;
        this.workflowsAsync = workflowsAsync;
        this.scheduledExecutorService =
            (executor instanceof ScheduledExecutorService)
                ? (ScheduledExecutorService) executor
                : Executors.newSingleThreadScheduledExecutor();
    }

    // Getter for the scheduled executor service
    ScheduledExecutorService getScheduledExecutor() {
        return this.scheduledExecutorService;
    }

    /**
     * Initiates the polling process by scheduling the first execution of the Poller.
     */
    public void startPolling() {
        log.info(
            "Poller [" +
            name +
            "] start: " +
            Thread.currentThread().getName() +
            " (ID: " +
            Thread.currentThread().getId() +
            ")"
        );
        getScheduledExecutor().schedule(this, delay, TimeUnit.SECONDS);
    }

    /**
     * Executes the polling logic. Depending on the configuration, workflows are executed either
     * synchronously or asynchronously.
     */
    @Override
    public void run() {
        log.info(
            "Poller [" +
            name +
            "] run: " +
            Thread.currentThread().getName() +
            " (ID: " +
            Thread.currentThread().getId() +
            ")"
        );

        // For the async workflows execution
        if (workflowsAsync) {
            executeAsync();
        } else { // Execute workflow one after one.
            executeSync();
        }
    }

    /**
     * Executes workflows synchronously one after the other.
     */
    private void executeSync() {
        if (active) {
            try {
                log.info(
                    "Workflow execution: " +
                    Thread.currentThread().getName() +
                    " (ID: " +
                    Thread.currentThread().getId() +
                    ")"
                );
                workflow.execute(null);
            } catch (Exception e) {
                if (e instanceof StopPoller) {
                    log.info("POLLER: " + e.getMessage());
                    stopPolling(); // Stop this Poller thread.
                } else {
                    log.error("POLLER EXCEPTION: " + e.getMessage());
                    stopPolling();
                }
            }
        }
        if (reschedule && active) {
            log.info(
                "Poller [" +
                name +
                "] reschedule: " +
                Thread.currentThread().getName() +
                " (ID: " +
                Thread.currentThread().getId() +
                ")"
            );
            log.info("--------------------------------------------------------------");

            // Delay the rescheduling to ensure all workflows have completed
            getScheduledExecutor().schedule(this::startPolling, delay, TimeUnit.SECONDS);
        }

        // if not reschedule but still active can stop immediately only one iteration.
        if (!reschedule && active) {
            stopPolling();
        }
    }

    /**
     * Executes steps in workflow in parallel way, with support for rescheduling after completion.
     */
    private void executeAsync() {
        // Execute the workflow asynchronously
        CompletableFuture<Void> workflowFuture = workflow.executeAsync(null);

        // Handle the completion of the workflow execution
        workflowFuture.whenCompleteAsync(
            (result, exception) -> {
                if (exception != null) {
                    if (exception instanceof CompletionException) {
                        Throwable cause = exception.getCause();
                        if (cause instanceof StopPoller) {
                            stopPolling(); // Stop this Poller thread.
                        } else {
                            log.info("POLLER EXCEPTION : " + exception.getMessage());
                            stopPolling();
                        }
                    } else {
                        log.info("POLLER EXCEPTION : " + exception.getMessage());
                        stopPolling();
                    }
                }

                if (reschedule && active) {
                    log.info(
                        "Poller [" +
                        name +
                        "] reschedule: " +
                        Thread.currentThread().getName() +
                        " (ID: " +
                        Thread.currentThread().getId() +
                        ")"
                    );
                    log.info("--------------------------------------------------------------");

                    // Delay the rescheduling to ensure all workflows have completed
                    getScheduledExecutor().schedule(this::startPolling, delay, TimeUnit.SECONDS);
                }
            },
            getScheduledExecutor()
        );
    }

    /**
     * Executes a single workflow asynchronously.
     *
     * @param workflow The workflow to be executed.
     * @return CompletableFuture representing the asynchronous execution.
     */

    /**
     * Stops the polling process. It shuts down the executor service and waits for its termination.
     * If termination does not occur within a specified timeout, a forced shutdown is attempted.
     */
    public void stopPolling() {
        if (active) {
            active = false;
            log.info(
                "Poller [" +
                name +
                "] stop: " +
                Thread.currentThread().getName() +
                " (ID: " +
                Thread.currentThread().getId() +
                ")"
            );
            getScheduledExecutor().shutdown();
            try {
                if (!getScheduledExecutor().awaitTermination(5, TimeUnit.SECONDS)) {
                    getScheduledExecutor().shutdownNow();
                    if (!getScheduledExecutor().awaitTermination(5, TimeUnit.SECONDS)) {
                        log.error("Unable to shutdown executor service :(");
                    }
                }
            } catch (InterruptedException e) {
                getScheduledExecutor().shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
