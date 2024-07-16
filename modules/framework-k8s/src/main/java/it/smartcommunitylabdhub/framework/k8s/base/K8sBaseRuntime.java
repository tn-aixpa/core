package it.smartcommunitylabdhub.framework.k8s.base;

import it.smartcommunitylabdhub.commons.models.base.ExecutableBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import it.smartcommunitylabdhub.commons.runtimes.base.AbstractBaseRuntime;
import it.smartcommunitylabdhub.framework.k8s.kubernetes.K8sBuilderHelper;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class K8sBaseRuntime<
    F extends ExecutableBaseSpec, S extends RunBaseSpec, Z extends RunBaseStatus, R extends K8sRunnable
>
    extends AbstractBaseRuntime<F, S, Z, R> {

    protected K8sBuilderHelper k8sBuilderHelper;

    protected K8sBaseRuntime(String kind) {
        super(kind);
    }

    @Autowired
    public void setK8sBuilderHelper(K8sBuilderHelper k8sBuilderHelper) {
        this.k8sBuilderHelper = k8sBuilderHelper;
    }
}
