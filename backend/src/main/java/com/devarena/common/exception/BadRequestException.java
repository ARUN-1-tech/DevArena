package com.devarena.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an invalid or malformed request is received.
 */
public class BadRequestException extends DevArenaException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }

    public BadRequestException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }
}
