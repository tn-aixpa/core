package it.smartcommunitylabdhub.commons.infrastructure;

import it.smartcommunitylabdhub.commons.exceptions.FrameworkException;

public interface Framework<R extends Runnable> {
    R execute(R runnable) throws FrameworkException;

    R stop(R runnable) throws FrameworkException;
    // String status(R runnable) throws FrameworkException;
}
