package com.colleful.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyExistResourceException extends RuntimeException {

    public AlreadyExistResourceException(String message) {
        super(message);
    }
}
