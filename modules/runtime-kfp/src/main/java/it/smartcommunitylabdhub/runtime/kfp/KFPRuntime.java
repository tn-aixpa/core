package it.smartcommunitylabdhub.runtime.kfp;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.models.base.Executable;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.services.entities.SecretService;
import it.smartcommunitylabdhub.framework.k8s.base.K8sBaseRuntime;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import it.smartcommunitylabdhub.runtime.kfp.runners.KFPPipelineRunner;
import it.smartcommunitylabdhub.runtime.kfp.specs.KFPPipelineTaskSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.KFPRunSpec;
import it.smartcommunitylabdhub.runtime.kfp.specs.KFPRunStatus;
import it.smartcommunitylabdhub.runtime.kfp.specs.KFPWorkflowSpec;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@RuntimeComponent(runtime = KFPRuntime.RUNTIME)
@Slf4j
public class KFPRuntime extends K8sBaseRuntime<KFPWorkflowSpec, KFPRunSpec, KFPRunStatus, K8sRunnable> {

    public static final String RUNTIME = "kfp";

    @Autowired
    SecretService secretService;

    @Value("${runtime.kfp.image}")
    private String image;

    // @Autowired(required = false)
    // private RunnableStore<K8sJobRunnable> jobRunnableStore;

    public KFPRuntime() {
        super(KFPRunSpec.KIND);
    }

    @Override
    public KFPRunSpec build(@NotNull Executable workflow, @NotNull Task task, @NotNull Run run) {
        if (!KFPRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), KFPRunSpec.KIND)
            );
        }

        KFPWorkflowSpec workSpec = new KFPWorkflowSpec(workflow.getSpec());
        KFPRunSpec runSpec = new KFPRunSpec(run.getSpec());

        String kind = task.getKind();

        //build task spec as defined
        TaskBaseSpec taskSpec =
            switch (kind) {
                case KFPPipelineTaskSpec.KIND -> {
                    yield new KFPPipelineTaskSpec(task.getSpec());
                }
                default -> throw new IllegalArgumentException(
                    "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task."
                );
            };

        //build run merging task spec overrides
        Map<String, Serializable> map = new HashMap<>();
        map.putAll(runSpec.toMap());
        taskSpec.toMap().forEach(map::putIfAbsent);

        KFPRunSpec kfpSpec = new KFPRunSpec(map);
        //ensure function is not modified
        kfpSpec.setWorkflowSpec(workSpec);

        return kfpSpec;
    }

    @Override
    public K8sRunnable run(@NotNull Run run) {
        //check run kind
        if (!KFPRunSpec.KIND.equals(run.getKind())) {
            throw new IllegalArgumentException(
                "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), KFPRunSpec.KIND)
            );
        }

        // Create spec for run
        KFPRunSpec runKfpSpec = new KFPRunSpec(run.getSpec());

        // Create string run accessor from task
        RunSpecAccessor runAccessor = RunUtils.parseTask(runKfpSpec.getTask());

        return switch (runAccessor.getTask()) {
            case KFPPipelineTaskSpec.KIND -> new KFPPipelineRunner(
                image,
                secretService.groupSecrets(run.getProject(), runKfpSpec.getTaskSpec().getSecrets())
            )
                .produce(run);
            default -> throw new IllegalArgumentException("Kind not recognized. Cannot retrieve the right Runner");
        };
    }
    // @Override
    // public K8sJobRunnable stop(Run run) {
    //     //check run kind
    //     if (!KFPRunSpec.KIND.equals(run.getKind())) {
    //         throw new IllegalArgumentException(
    //             "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), KFPRunSpec.KIND)
    //         );
    //     }

    //     if (jobRunnableStore == null) {
    //         throw new CoreRuntimeException("Job Store is not available");
    //     }

    //     try {
    //         K8sJobRunnable k8sJobRunnable = jobRunnableStore.find(run.getId());
    //         if (k8sJobRunnable == null) {
    //             throw new NoSuchEntityException("JobRunnable not found");
    //         }

    //         //set state to STOP to signal framework to stop the runnable
    //         k8sJobRunnable.setState(State.STOP.name());

    //         return k8sJobRunnable;
    //     } catch (StoreException e) {
    //         log.error("Error stopping run", e);
    //         throw new NoSuchEntityException("Error stopping run", e);
    //     }
    // }

    // @Override
    // public K8sJobRunnable delete(Run run) {
    //     //check run kind
    //     if (!KFPRunSpec.KIND.equals(run.getKind())) {
    //         throw new IllegalArgumentException(
    //             "Run kind {} unsupported, expecting {}".formatted(String.valueOf(run.getKind()), KFPRunSpec.KIND)
    //         );
    //     }

    //     if (jobRunnableStore == null) {
    //         throw new CoreRuntimeException("Job Store is not available");
    //     }
    //     try {
    //         K8sJobRunnable k8sJobRunnable = jobRunnableStore.find(run.getId());
    //         if (k8sJobRunnable == null) {
    //             throw new NoSuchEntityException("JobRunnable not found");
    //         }

    //         //set state to DELETING to signal framework to delete the runnable
    //         k8sJobRunnable.setState(State.DELETING.name());

    //         return k8sJobRunnable;
    //     } catch (StoreException e) {
    //         log.error("Error stopping run", e);
    //         throw new NoSuchEntityException("Error stopping run", e);
    //     }
    // }

    // @Override
    // public KfpRunStatus onDeleted(Run run, RunRunnable runnable) {
    //     if (runnable != null) {
    //         try {
    //             if (jobRunnableStore != null && jobRunnableStore.find(runnable.getId()) != null) {
    //                 jobRunnableStore.remove(runnable.getId());
    //             }
    //         } catch (StoreException e) {
    //             log.error("Error deleting runnable", e);
    //             throw new NoSuchEntityException("Error deleting runnable", e);
    //         }
    //     }

    //     return null;
    // }
}
