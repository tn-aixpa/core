package it.smartcommunitylabdhub.commons.exceptions;

public class StopPoller extends RuntimeException {

    public StopPoller(String message) {
        super(message);
    }
}
