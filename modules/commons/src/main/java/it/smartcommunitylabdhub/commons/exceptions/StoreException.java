package it.smartcommunitylabdhub.commons.exceptions;

public class StoreException extends Exception {

    public StoreException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public StoreException(String msg) {
        super(msg);
    }
}
