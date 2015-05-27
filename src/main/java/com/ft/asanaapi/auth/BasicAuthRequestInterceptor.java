package com.ft.asanaapi.auth;

import retrofit.RequestInterceptor;

import java.util.Base64;

/**
 * This class is used to add basic http auth to asana api requests.
 */
public class BasicAuthRequestInterceptor implements RequestInterceptor {

    private String password;

    @Override
    public void intercept(RequestFacade requestFacade) {

        if (password != null) {
            final String authorizationValue = encodeCredentialsForBasicAuthorization();
            requestFacade.addHeader("Authorization", authorizationValue);
        }
    }

    private String encodeCredentialsForBasicAuthorization() {
        final String userAndPassword =  password + ":";
        return "Basic " + Base64.getEncoder().encodeToString(userAndPassword.getBytes());
    }

    public BasicAuthRequestInterceptor setPassword(String password) {
        this.password = password;
        return this;
    }
}
