package it.smartcommunitylabdhub.commons.infrastructure;

import it.smartcommunitylabdhub.commons.exceptions.FrameworkException;

public interface Framework<R extends Runnable> {
    void execute(R runnable) throws FrameworkException;

    void stop(R runnable) throws FrameworkException;
    // String status(R runnable) throws FrameworkException;
}
