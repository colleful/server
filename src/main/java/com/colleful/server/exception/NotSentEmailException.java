package com.colleful.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class NotSentEmailException extends RuntimeException {

    public NotSentEmailException(String message) {
        super(message);
    }
}
