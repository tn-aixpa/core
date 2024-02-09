package it.smartcommunitylabdhub.commons.exceptions;

import lombok.Data;

@Data
public class ErrorResponse {
    private int status;
    private String message;
    private String errorCode;
}
