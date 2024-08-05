package it.smartcommunitylabdhub.core.fsm;

public enum RunEvent {
    BUILD,
    LOOP,
    RUN,
    EXECUTE,
    PENDING,
    DELETING,
    COMPLETE,
    ERROR,
    STOP,
}
