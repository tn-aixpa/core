package it.smartcommunitylabdhub.commons.exceptions;

//TODO checked exception
public class SystemException extends RuntimeException {

    public SystemException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SystemException(String msg) {
        super(msg);
    }
}
