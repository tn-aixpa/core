package it.smartcommunitylabdhub.core.fsm;

public enum RunEvent {
    BUILD,
    COMPLETE,
    DELETING,
    ERROR,
    EXECUTE,
    LOOP,
    PENDING,
    RESUME,
    RUN,
    STOP,
}
