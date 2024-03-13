package it.smartcommunitylabdhub.commons.exceptions;

import java.text.MessageFormat;

public class NoSuchEntityException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "No such {0}.";

    public NoSuchEntityException() {
        super(build("entity"));
    }

    public NoSuchEntityException(String entity) {
        super(build(entity));
    }

    public NoSuchEntityException(Throwable cause) {
        super(build("entity"), cause);
    }

    public NoSuchEntityException(String entity, Throwable cause) {
        super(build(entity), cause);
    }

    private static String build(String entity) {
        return MessageFormat.format(DEFAULT_MESSAGE, entity);
    }
}
