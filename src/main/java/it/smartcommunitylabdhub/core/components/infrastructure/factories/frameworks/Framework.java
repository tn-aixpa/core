/**
 * Framework.java
 */

package it.smartcommunitylabdhub.core.components.infrastructure.factories.frameworks;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.Runnable;

public interface Framework<R extends Runnable> {
    void execute(R runnable);

    void stop(R runnable);

    String status(R runnable);
}
