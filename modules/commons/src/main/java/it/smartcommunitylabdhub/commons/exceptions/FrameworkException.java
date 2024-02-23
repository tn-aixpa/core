package it.smartcommunitylabdhub.commons.exceptions;

public abstract class FrameworkException extends Exception {

    protected FrameworkException(String msg, Throwable cause) {
        super(msg, cause);
    }

    protected FrameworkException(String msg) {
        super(msg);
    }
}
