package it.smartcommunitylabdhub.runtime.python;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.exceptions.CoreRuntimeException;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.infrastructure.Runtime;
import it.smartcommunitylabdhub.commons.models.base.Executable;
import it.smartcommunitylabdhub.commons.models.entities.log.Log;
import it.smartcommunitylabdhub.commons.models.entities.log.LogSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.services.LogService;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.commons.services.entities.FunctionService;
import it.smartcommunitylabdhub.commons.services.entities.SecretService;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sJobFramework;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sServeFramework;
import it.smartcommunitylabdhub.framework.k8s.model.K8sLogStatus;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreLog;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreMetric;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sServeRunnable;
import it.smartcommunitylabdhub.runtime.python.builders.PythonJobBuilder;
import it.smartcommunitylabdhub.runtime.python.builders.PythonServeBuilder;
import it.smartcommunitylabdhub.runtime.python.runners.PythonJobRunner;
import it.smartcommunitylabdhub.runtime.python.runners.PythonServeRunner;
import it.smartcommunitylabdhub.runtime.python.specs.function.PythonFunctionSpec;
import it.smartcommunitylabdhub.runtime.python.specs.run.PythonRunSpec;
import it.smartcommunitylabdhub.runtime.python.specs.task.PythonJobTaskSpec;
import it.smartcommunitylabdhub.runtime.python.specs.task.PythonServeTaskSpec;
import it.smartcommunitylabdhub.runtime.python.status.PythonRunStatus;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;

@Slf4j
@RuntimeComponent(runtime = PythonRuntime.RUNTIME)
public class PythonRuntime implements Runtime<PythonFunctionSpec, PythonRunSpec, PythonRunStatus, RunRunnable> {

    public static final String RUNTIME = "python";

    //TODO make configurable
    public static final int MAX_METRICS = 300;

    private final PythonJobBuilder jobBuilder = new PythonJobBuilder();
    private final PythonServeBuilder serveBuilder = new PythonServeBuilder();

    @Autowired
    private SecretService secretService;

    @Autowired(required = false)
    private RunnableStore<K8sJobRunnable> jobRunnableStore;

    @Autowired(required = false)
    private RunnableStore<K8sServeRunnable> serveRunnableStore;

    @Autowired
    private FunctionService functionService;

    @Autowired
    private LogService logService;

    @Value("${runtime.python.image}")
    private String image;

    @Value("${runtime.python.command}")
    private String command;

    @Override
    public PythonRunSpec build(@NotNull Executable function, @NotNull Task task, @NotNull Run run) {
        //check run kind
        if (!PythonRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), PythonRunSpec.KIND)
            );
        }

        PythonFunctionSpec funSpec = new PythonFunctionSpec(function.getSpec());
        PythonRunSpec runSpec = new PythonRunSpec(run.getSpec());

        String kind = task.getKind();

        // Retrieve builder using task kind
        switch (kind) {
            case PythonJobTaskSpec.KIND -> {
                PythonJobTaskSpec taskJobSpec = new PythonJobTaskSpec(task.getSpec());
                return jobBuilder.build(funSpec, taskJobSpec, runSpec);
            }
            case PythonServeTaskSpec.KIND -> {
                PythonServeTaskSpec taskServeSpec = new PythonServeTaskSpec(task.getSpec());
                return serveBuilder.build(funSpec, taskServeSpec, runSpec);
            }
            default -> throw new IllegalArgumentException(
                "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task."
            );
        }
    }

    @Override
    public RunRunnable run(@NotNull Run run) {
        //check run kind
        if (!PythonRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), PythonRunSpec.KIND)
            );
        }

        PythonRunSpec runPythonSpec = new PythonRunSpec(run.getSpec());

        // Create string run accessor from task
        RunSpecAccessor runAccessor = RunUtils.parseTask(runPythonSpec.getTask());

        return switch (runAccessor.getTask()) {
            case PythonJobTaskSpec.KIND -> new PythonJobRunner(
                image,
                command,
                runPythonSpec.getFunctionSpec(),
                secretService.groupSecrets(run.getProject(), runPythonSpec.getTaskJobSpec().getSecrets())
            )
                .produce(run);
            case PythonServeTaskSpec.KIND -> new PythonServeRunner(
                image,
                command,
                runPythonSpec.getFunctionSpec(),
                secretService.groupSecrets(run.getProject(), runPythonSpec.getTaskJobSpec().getSecrets())
            )
                .produce(run);
            default -> throw new IllegalArgumentException("Kind not recognized. Cannot retrieve the right Runner");
        };
    }

    @Override
    public RunRunnable stop(Run run) throws NoSuchEntityException {
        //check run kind
        if (!PythonRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), PythonRunSpec.KIND)
            );
        }

        PythonRunSpec runPythonSpec = new PythonRunSpec(run.getSpec());

        // Create string run accessor from task
        RunSpecAccessor runAccessor = RunUtils.parseTask(runPythonSpec.getTask());

        try {
            return switch (runAccessor.getTask()) {
                case PythonJobTaskSpec.KIND -> {
                    if (jobRunnableStore != null) {
                        K8sJobRunnable k8sJobRunnable = jobRunnableStore.find(run.getId());
                        if (k8sJobRunnable == null) {
                            throw new NoSuchEntityException("Run not found");
                        }
                        k8sJobRunnable.setState(State.STOP.name());
                        yield k8sJobRunnable;
                    }
                    throw new CoreRuntimeException("Store is not available");
                }
                case PythonServeTaskSpec.KIND -> {
                    if (serveRunnableStore != null) {
                        K8sServeRunnable k8sServeRunnable = serveRunnableStore.find(run.getId());
                        if (k8sServeRunnable == null) {
                            throw new NoSuchEntityException("Run not found");
                        }
                        k8sServeRunnable.setState(State.STOP.name());
                        yield k8sServeRunnable;
                    }
                    throw new CoreRuntimeException("Store is not available");
                }
                default -> throw new IllegalArgumentException("Kind not recognized. Cannot retrieve the right Runner");
            };
        } catch (StoreException e) {
            log.error("Error stopping run", e);
            throw new NoSuchEntityException("Error stopping run", e);
        }
    }

    @Override
    public RunRunnable delete(Run run) throws NoSuchEntityException {
        //check run kind
        if (!PythonRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), PythonRunSpec.KIND)
            );
        }

        PythonRunSpec runPythonSpec = new PythonRunSpec(run.getSpec());

        // Create string run accessor from task
        RunSpecAccessor runAccessor = RunUtils.parseTask(runPythonSpec.getTask());

        try {
            return switch (runAccessor.getTask()) {
                case PythonJobTaskSpec.KIND -> {
                    if (jobRunnableStore != null) {
                        K8sJobRunnable k8sJobRunnable = jobRunnableStore.find(run.getId());
                        if (k8sJobRunnable == null) {
                            //not in store, either not existent or already removed, nothing to do
                            yield null;
                        }
                        k8sJobRunnable.setState(State.DELETING.name());
                        yield k8sJobRunnable;
                    }
                    throw new CoreRuntimeException("Job Store is not available");
                }
                case PythonServeTaskSpec.KIND -> {
                    if (jobRunnableStore != null) {
                        K8sServeRunnable k8sServeRunnable = serveRunnableStore.find(run.getId());
                        if (k8sServeRunnable == null) {
                            //not in store, either not existent or already removed, nothing to do
                            yield null;
                        }
                        k8sServeRunnable.setState(State.DELETING.name());
                        yield k8sServeRunnable;
                    }
                    throw new CoreRuntimeException("Job Store is not available");
                }
                default -> throw new IllegalArgumentException("Kind not recognized. Cannot retrieve the right Runner");
            };
        } catch (StoreException e) {
            log.error("Error deleting run", e);
            throw new NoSuchEntityException("Error deleting run", e);
        }
    }

    @Override
    public PythonRunStatus onRunning(Run run, RunRunnable runnable) {
        //extract info for status
        if (runnable instanceof K8sRunnable) {
            Map<String, Serializable> res = ((K8sRunnable) runnable).getResults();
            //extract k8s details
            //TODO

            //extract logs
            List<CoreLog> logs = ((K8sRunnable) runnable).getLogs();
            List<CoreMetric> metrics = ((K8sRunnable) runnable).getMetrics();

            if (logs != null) {
                writeLogs(run, logs, metrics);
            }

            //dump as-is
            return PythonRunStatus.builder().k8s(res).build();
        }

        return null;
    }

    @Override
    public PythonRunStatus onComplete(Run run, RunRunnable runnable) {
        if (runnable != null) {
            cleanup(runnable);
        }

        PythonRunSpec runPythonSpec = new PythonRunSpec(run.getSpec());

        //extract info for status
        if (runnable instanceof K8sRunnable) {
            Map<String, Serializable> res = ((K8sRunnable) runnable).getResults();
            //extract k8s details
            //TODO

            //extract logs
            List<CoreLog> logs = ((K8sRunnable) runnable).getLogs();
            List<CoreMetric> metrics = ((K8sRunnable) runnable).getMetrics();

            if (logs != null) {
                writeLogs(run, logs, metrics);
            }

            //dump as-is
            return PythonRunStatus.builder().k8s(res).build();
        }

        return null;
    }

    @Override
    public PythonRunStatus onError(Run run, RunRunnable runnable) {
        if (runnable != null) {
            cleanup(runnable);
        }

        //extract info for status
        if (runnable instanceof K8sRunnable) {
            Map<String, Serializable> res = ((K8sRunnable) runnable).getResults();
            //extract k8s details
            //TODO

            //extract logs
            List<CoreLog> logs = ((K8sRunnable) runnable).getLogs();
            List<CoreMetric> metrics = ((K8sRunnable) runnable).getMetrics();

            if (logs != null) {
                writeLogs(run, logs, metrics);
            }

            //dump as-is
            return PythonRunStatus.builder().k8s(res).build();
        }

        return null;
    }

    @Override
    public PythonRunStatus onStopped(Run run, RunRunnable runnable) {
        if (runnable != null) {
            cleanup(runnable);
        }

        //extract info for status
        if (runnable instanceof K8sRunnable) {
            Map<String, Serializable> res = ((K8sRunnable) runnable).getResults();
            //extract k8s details
            //TODO

            //extract logs
            List<CoreLog> logs = ((K8sRunnable) runnable).getLogs();
            List<CoreMetric> metrics = ((K8sRunnable) runnable).getMetrics();

            if (logs != null) {
                writeLogs(run, logs, metrics);
            }

            //dump as-is
            return PythonRunStatus.builder().k8s(res).build();
        }

        return null;
    }

    @Override
    public PythonRunStatus onDeleted(Run run, RunRunnable runnable) {
        if (runnable != null) {
            cleanup(runnable);
        }

        return null;
    }

    private void writeLogs(Run run, List<CoreLog> logs, List<CoreMetric> metrics) {
        String runId = run.getId();
        Instant now = Instant.now();

        //logs are grouped by pod+container, search by run and create/append
        Map<String, Log> entries = logService
            .getLogsByRunId(runId)
            .stream()
            .map(e -> {
                K8sLogStatus status = new K8sLogStatus();
                status.configure(e.getStatus());

                String pod = status.getPod() != null ? status.getPod() : "";
                String container = status.getContainer() != null ? status.getContainer() : "";
                String namespace = status.getNamespace() != null ? status.getNamespace() : "";
                String key = namespace + pod + container;

                if (StringUtils.hasText(runId)) {
                    return Map.entry(key, e);
                } else {
                    return null;
                }
            })
            .filter(e -> e != null)
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        //reformat metrics grouped per container
        //TODO refactor
        Map<String, HashMap<String, Serializable>> mmetrics = new HashMap<>();
        if (metrics != null) {
            metrics.forEach(m -> {
                if (m.metrics() != null) {
                    m
                        .metrics()
                        .forEach(cm -> {
                            String key = m.namespace() + m.pod() + cm.getName();
                            if (cm.getUsage() != null) {
                                HashMap<String, String> usage = cm
                                    .getUsage()
                                    .entrySet()
                                    .stream()
                                    .collect(
                                        Collectors.toMap(
                                            e -> e.getKey(),
                                            e -> e.getValue().toSuffixedString(),
                                            (prev, next) -> next,
                                            HashMap::new
                                        )
                                    );

                                HashMap<String, Serializable> mm = new HashMap<>();
                                mm.put("timestamp", m.timestamp());
                                mm.put("window", m.window());
                                mm.put("usage", usage);
                                mmetrics.put(key, mm);
                            }
                        });
                }
            });
        }

        logs.forEach(l -> {
            try {
                String key = l.namespace() + l.pod() + l.container();

                if (entries.get(key) != null) {
                    //update
                    Log log = entries.get(key);
                    log.setContent(l.value());

                    //check if metric is available
                    if (mmetrics.containsKey(key)) {
                        HashMap<String, Serializable> metric = mmetrics.get(key);

                        //append to status
                        K8sLogStatus logStatus = new K8sLogStatus();
                        logStatus.configure(log.getStatus());

                        List<Serializable> list = logStatus.getMetrics() != null
                            ? new ArrayList<>(logStatus.getMetrics())
                            : new ArrayList<>();

                        list.addLast(metric);
                        logStatus.setMetrics(list);

                        //check if we need to slice
                        //TODO cleanup
                        if (list.size() > MAX_METRICS) {
                            Collections.reverse(list);
                            List<Serializable> slice = new ArrayList<>(list.subList(0, MAX_METRICS));
                            Collections.reverse(slice);
                            logStatus.setMetrics(slice);
                        }

                        log.setStatus(logStatus.toMap());
                    }

                    logService.updateLog(log.getId(), log);
                } else {
                    //add as new
                    LogSpec logSpec = new LogSpec();
                    logSpec.setRun(runId);
                    logSpec.setTimestamp(now.toEpochMilli());

                    K8sLogStatus logStatus = new K8sLogStatus();
                    logStatus.setPod(l.pod());
                    logStatus.setContainer(l.container());
                    logStatus.setNamespace(l.namespace());

                    //check if metric is available
                    if (mmetrics.containsKey(key)) {
                        HashMap<String, Serializable> metric = mmetrics.get(key);

                        //append to status
                        List<Serializable> list = logStatus.getMetrics() != null
                            ? new ArrayList<>(logStatus.getMetrics())
                            : new ArrayList<>();
                        list.addLast(metric);
                        logStatus.setMetrics(list);

                        //check if we need to slice
                        //TODO cleanup
                        if (list.size() > MAX_METRICS) {
                            Collections.reverse(list);
                            List<Serializable> slice = new ArrayList<>(list.subList(0, MAX_METRICS));
                            Collections.reverse(slice);
                            logStatus.setMetrics(slice);
                        }
                    }

                    Log log = Log
                        .builder()
                        .project(run.getProject())
                        .spec(logSpec.toMap())
                        .status(logStatus.toMap())
                        .content(l.value())
                        .build();

                    logService.createLog(log);
                }
            } catch (
                NoSuchEntityException
                | IllegalArgumentException
                | SystemException
                | BindException
                | DuplicatedEntityException e
            ) {
                //invalid, skip
                //TODO handle
            }
        });
    }

    private void cleanup(RunRunnable runnable) {
        try {
            RunnableStore<?> store = getStore(runnable);
            if (store != null && store.find(runnable.getId()) != null) {
                store.remove(runnable.getId());
            }
        } catch (StoreException e) {
            log.error("Error deleting runnable", e);
            throw new NoSuchEntityException("Error deleting runnable", e);
        }
    }

    private RunnableStore<?> getStore(RunRunnable runnable) {
        return switch (runnable.getFramework()) {
            case K8sJobFramework.FRAMEWORK -> {
                yield jobRunnableStore;
            }
            case K8sServeFramework.FRAMEWORK -> {
                yield serveRunnableStore;
            }
            default -> null;
        };
    }
}
