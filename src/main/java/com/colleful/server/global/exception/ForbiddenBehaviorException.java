package com.colleful.server.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenBehaviorException extends RuntimeException {

    public ForbiddenBehaviorException(ErrorType type) {
        super(type.getMessage());
    }
}
