package com.devarena.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base unchecked exception for DevArena platform domain errors.
 */
public class DevArenaException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public DevArenaException(String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
    }

    public DevArenaException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
