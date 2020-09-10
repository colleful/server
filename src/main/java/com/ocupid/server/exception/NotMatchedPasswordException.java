package com.ocupid.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NotMatchedPasswordException extends RuntimeException {

    public NotMatchedPasswordException(String message) {
        super(message);
    }
}
