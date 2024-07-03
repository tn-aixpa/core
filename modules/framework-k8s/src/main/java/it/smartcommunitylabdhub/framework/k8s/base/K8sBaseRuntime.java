package it.smartcommunitylabdhub.framework.k8s.base;

import it.smartcommunitylabdhub.commons.models.base.ExecutableBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import it.smartcommunitylabdhub.commons.runtimes.base.AbstractBaseRuntime;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;

public abstract class K8sBaseRuntime<
    F extends ExecutableBaseSpec, S extends RunBaseSpec, Z extends RunBaseStatus, R extends K8sRunnable
>
    extends AbstractBaseRuntime<F, S, Z, R> {

    protected K8sBaseRuntime(String kind) {
        super(kind);
    }
}
