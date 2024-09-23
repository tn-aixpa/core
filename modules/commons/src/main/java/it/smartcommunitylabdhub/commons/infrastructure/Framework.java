package it.smartcommunitylabdhub.commons.infrastructure;

import it.smartcommunitylabdhub.commons.exceptions.FrameworkException;

public interface Framework<R extends RunRunnable> {
    R run(R runnable) throws FrameworkException;

    R stop(R runnable) throws FrameworkException;
    R resume(R runnable) throws FrameworkException;

    R delete(R runnable) throws FrameworkException;
}
