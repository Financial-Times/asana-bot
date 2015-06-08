package com.ft.asanaapi.auth;

public class InvalidDomainException extends RuntimeException {
    public InvalidDomainException() {
        super("Only FT account are authorized");
    }
}
