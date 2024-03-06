package it.smartcommunitylabdhub.fsm.exceptions;

import java.text.MessageFormat;

public class InvalidTransactionException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "Invalid transaction from state {0} to state {1}.";

    public InvalidTransactionException() {
        super(build("StateA", "StateB"));
    }

    public InvalidTransactionException(String from, String to) {
        super(build(from, to));
    }

    public InvalidTransactionException(Throwable cause) {
        super(build("StateA", "StateB"), cause);
    }


    public InvalidTransactionException(String from, String to, Throwable cause) {
        super(build(from, to), cause);
    }

    private static String build(String from, String to) {
        return MessageFormat.format(DEFAULT_MESSAGE, from, to);
    }
}
