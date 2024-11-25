package it.smartcommunitylabdhub.fsm.exceptions;

import java.text.MessageFormat;
import lombok.Getter;

public class InvalidTransitionException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "Invalid transition from state {0} to state {1}.";

    @Getter
    private final String fromState;

    @Getter
    private final String toState;

    public InvalidTransitionException() {
        super(build("StateA", "StateB"));
        this.fromState = "StateA";
        this.toState = "StateB";
    }

    public InvalidTransitionException(String from, String to) {
        super(build(from, to));
        this.fromState = from;
        this.toState = to;
    }

    public InvalidTransitionException(Throwable cause) {
        super(build("StateA", "StateB"), cause);
        this.fromState = "StateA";
        this.toState = "StateB";
    }

    public InvalidTransitionException(String from, String to, Throwable cause) {
        super(build(from, to), cause);
        this.fromState = from;
        this.toState = to;
    }

    private static String build(String from, String to) {
        return MessageFormat.format(DEFAULT_MESSAGE, from, to);
    }
}
