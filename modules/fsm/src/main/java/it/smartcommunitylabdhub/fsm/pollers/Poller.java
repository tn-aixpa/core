package it.smartcommunitylabdhub.fsm.pollers;

import it.smartcommunitylabdhub.fsm.exceptions.StopPoller;
import it.smartcommunitylabdhub.fsm.workflow.Workflow;
import java.util.List;
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
    private final List<Workflow> workflowList;

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
     * @param workflowList   List of workflows to be executed.
     * @param delay          Delay between consecutive polling runs in seconds.
     * @param reschedule     Flag indicating whether to reschedule after each run.
     * @param workflowsAsync Flag indicating whether workflows should be executed asynchronously.
     * @param executor       Task executor for handling asynchronous workflow execution.
     */
    public Poller(
        String name,
        List<Workflow> workflowList,
        long delay,
        boolean reschedule,
        boolean workflowsAsync,
        TaskExecutor executor
    ) {
        this.name = name;
        this.workflowList = workflowList;
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
        for (Workflow workflow : workflowList) {
            if (!active) {
                break;
            }
            executeWorkflow(workflow);
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
     * Executes workflows asynchronously, with support for rescheduling after completion.
     */
    private void executeAsync() {
        CompletableFuture<Object> allWorkflowsFuture = CompletableFuture.completedFuture(null);

        // Execute the workflows sequentially
        for (Workflow workflow : workflowList) {
            if (active) {
                allWorkflowsFuture =
                    allWorkflowsFuture.thenComposeAsync(
                        result -> executeWorkflowAsync(workflow),
                        getScheduledExecutor()
                    );
            } else {
                break;
            }
        }

        allWorkflowsFuture.whenCompleteAsync(
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
        ); // Specify the executor for the continuation
    }

    /**
     * Executes a single workflow synchronously.
     *
     * @param workflow The workflow to be executed.
     */
    private void executeWorkflow(Workflow workflow) {
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

    /**
     * Executes a single workflow asynchronously.
     *
     * @param workflow The workflow to be executed.
     * @return CompletableFuture representing the asynchronous execution.
     */
    private CompletableFuture<Object> executeWorkflowAsync(Workflow workflow) {
        CompletableFuture<Object> workflowExecution = new CompletableFuture<>();

        CompletableFuture
            .supplyAsync(
                () -> {
                    try {
                        log.info(
                            "Workflow Execution: " +
                            Thread.currentThread().getName() +
                            " (ID: " +
                            Thread.currentThread().getId() +
                            ")"
                        );
                        return workflow.execute(null);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                },
                getScheduledExecutor()
            )
            .whenComplete((result, exception) -> {
                if (exception != null) {
                    workflowExecution.completeExceptionally(exception);
                } else {
                    workflowExecution.complete(result);
                }
            });

        return workflowExecution;
    }

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
