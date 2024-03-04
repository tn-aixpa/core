package it.smartcommunitylabdhub.commons.exceptions;

public class FrameworkException extends Exception {

    public FrameworkException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public FrameworkException(String msg) {
        super(msg);
    }
}
