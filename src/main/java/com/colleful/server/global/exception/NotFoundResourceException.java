package com.colleful.server.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundResourceException extends RuntimeException {

    public NotFoundResourceException(ErrorType type) {
        super(type.getMessage());
    }
}
