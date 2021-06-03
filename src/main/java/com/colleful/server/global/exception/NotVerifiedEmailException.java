package com.colleful.server.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NotVerifiedEmailException extends RuntimeException {

    public NotVerifiedEmailException() {
        super("인증되지 않은 이메일입니다.");
    }
}
