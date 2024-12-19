package com.colleful.server.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidCodeException extends RuntimeException {

    public InvalidCodeException() {
        super("인증번호가 일치하지 않습니다.");
    }
}
