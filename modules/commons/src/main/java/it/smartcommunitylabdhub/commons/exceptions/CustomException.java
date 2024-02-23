package it.smartcommunitylabdhub.commons.exceptions;

import lombok.Getter;

//TODO replace with dedicated exceptions
@Deprecated
@Getter
public class CustomException extends RuntimeException {

    private final Exception innerException;

    public CustomException(String message, Exception innerException) {
        super(message);
        this.innerException = innerException;
    }
}
