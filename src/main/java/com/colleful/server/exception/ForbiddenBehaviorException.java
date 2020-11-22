package com.colleful.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenBehaviorException extends RuntimeException {

    public ForbiddenBehaviorException(String message) {
        super(message);
    }
}
