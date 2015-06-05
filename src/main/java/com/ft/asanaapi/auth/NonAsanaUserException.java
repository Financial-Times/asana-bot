package com.ft.asanaapi.auth;

public class NonAsanaUserException extends RuntimeException {
    public NonAsanaUserException() {
        super("FT user is not registered for Asana");
    }
}
