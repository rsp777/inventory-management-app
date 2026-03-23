package com.pawar.inventory.app.exception;

import org.springframework.http.HttpStatus;

import com.pawar.inventory.app.exception.base.BaseException;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }
}
