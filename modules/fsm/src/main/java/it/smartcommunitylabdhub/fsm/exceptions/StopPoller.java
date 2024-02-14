package it.smartcommunitylabdhub.fsm.exceptions;

public class StopPoller extends RuntimeException {

    public StopPoller(String message) {
        super(message);
    }
}
