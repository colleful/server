package com.colleful.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NotVerifiedEmailException extends RuntimeException {

    public NotVerifiedEmailException(String message) {
        super(message);
    }
}
