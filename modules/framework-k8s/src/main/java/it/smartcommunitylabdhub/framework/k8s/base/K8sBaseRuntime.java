package it.smartcommunitylabdhub.framework.k8s.base;

import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.models.base.ExecutableBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import it.smartcommunitylabdhub.commons.runtimes.base.AbstractBaseRuntime;
import it.smartcommunitylabdhub.framework.k8s.kubernetes.K8sBuilderHelper;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class K8sBaseRuntime<
    F extends ExecutableBaseSpec, S extends RunBaseSpec, Z extends RunBaseStatus, R extends K8sRunnable
>
    extends AbstractBaseRuntime<F, S, Z, R> {

    protected K8sBuilderHelper k8sBuilderHelper;

    protected K8sBaseRuntime(String kind) {
        super(kind);
    }

    @Autowired(required = false)
    public void setK8sBuilderHelper(K8sBuilderHelper k8sBuilderHelper) {
        this.k8sBuilderHelper = k8sBuilderHelper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Z onRunning(@NotNull Run run, RunRunnable runnable) {
        if (runnable != null && runnable instanceof K8sRunnable) {
            K8sRunnable k8sRunnable = (K8sRunnable) runnable;
            RunBaseStatus status = RunBaseStatus
                .baseBuilder()
                .state(k8sRunnable.getState())
                .message(k8sRunnable.getMessage())
                .build();

            return (Z) status;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Z onComplete(@NotNull Run run, RunRunnable runnable) {
        if (runnable != null && runnable instanceof K8sRunnable) {
            K8sRunnable k8sRunnable = (K8sRunnable) runnable;
            RunBaseStatus status = RunBaseStatus
                .baseBuilder()
                .state(k8sRunnable.getState())
                .message(k8sRunnable.getMessage())
                .build();

            return (Z) status;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Z onError(@NotNull Run run, RunRunnable runnable) {
        if (runnable != null && runnable instanceof K8sRunnable) {
            K8sRunnable k8sRunnable = (K8sRunnable) runnable;
            RunBaseStatus status = RunBaseStatus
                .baseBuilder()
                .state(k8sRunnable.getState())
                .message(k8sRunnable.getError())
                .build();

            return (Z) status;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Z onStopped(@NotNull Run run, RunRunnable runnable) {
        if (runnable != null && runnable instanceof K8sRunnable) {
            K8sRunnable k8sRunnable = (K8sRunnable) runnable;
            RunBaseStatus status = RunBaseStatus
                .baseBuilder()
                .state(k8sRunnable.getState())
                .message(k8sRunnable.getMessage())
                .build();

            return (Z) status;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Z onDeleted(@NotNull Run run, RunRunnable runnable) {
        if (runnable != null && runnable instanceof K8sRunnable) {
            K8sRunnable k8sRunnable = (K8sRunnable) runnable;
            RunBaseStatus status = RunBaseStatus
                .baseBuilder()
                .state(k8sRunnable.getState())
                .message(k8sRunnable.getMessage())
                .build();

            return (Z) status;
        }

        return null;
    }
}
