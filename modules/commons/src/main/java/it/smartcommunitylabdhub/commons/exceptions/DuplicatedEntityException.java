package it.smartcommunitylabdhub.commons.exceptions;

import java.text.MessageFormat;

public class DuplicatedEntityException extends Exception {

    public static final String DEFAULT_MESSAGE = "Duplicated entity {0}:{1}.";

    private static String build(String entity, String id) {
        return MessageFormat.format(DEFAULT_MESSAGE, entity, id);
    }

    public DuplicatedEntityException(String id) {
        super(build("entity", id));
    }

    public DuplicatedEntityException(String entity, String id) {
        super(build(entity, id));
    }

    public DuplicatedEntityException(String id, Throwable cause) {
        super(build("entity", id), cause);
    }

    public DuplicatedEntityException(String entity, String id, Throwable cause) {
        super(build(entity, id), cause);
    }
}
