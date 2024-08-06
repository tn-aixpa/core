package it.smartcommunitylabdhub.authorization.exceptions;

public class JwtTokenServiceException extends RuntimeException {

    public JwtTokenServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
