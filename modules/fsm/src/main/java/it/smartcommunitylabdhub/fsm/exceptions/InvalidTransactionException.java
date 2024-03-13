package it.smartcommunitylabdhub.fsm.exceptions;

import java.text.MessageFormat;
import lombok.Getter;

public class InvalidTransactionException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "Invalid transaction from state {0} to state {1}.";

    @Getter
    private final String fromState;

    @Getter
    private final String toState;

    public InvalidTransactionException() {
        super(build("StateA", "StateB"));
        this.fromState = "StateA";
        this.toState = "StateB";
    }

    public InvalidTransactionException(String from, String to) {
        super(build(from, to));
        this.fromState = from;
        this.toState = to;
    }

    public InvalidTransactionException(Throwable cause) {
        super(build("StateA", "StateB"), cause);
        this.fromState = "StateA";
        this.toState = "StateB";
    }

    public InvalidTransactionException(String from, String to, Throwable cause) {
        super(build(from, to), cause);
        this.fromState = from;
        this.toState = to;
    }

    private static String build(String from, String to) {
        return MessageFormat.format(DEFAULT_MESSAGE, from, to);
    }
}
