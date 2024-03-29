package com.colleful.server.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyExistResourceException extends RuntimeException {

    public AlreadyExistResourceException(ErrorType type) {
        super(type.getMessage());
    }
}
