package com.ft.asanaapi.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AuthorizationFailureHandler {

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(InvalidDomainException.class)
    public void handleInvalidDomainException() {
        // TODO de-authorize
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NonAsanaUserException.class)
    public void handleNonAsanaUserException() {
        // TODO de-authorize
    }

}
