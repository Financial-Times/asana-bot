package com.ft.asanaapi.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DomainValidator {

    private static final String FT_DOMAIN = "ft.com";
    private static final String AUTHORIZATION_FAILURE_REASON = "Authorization failed due to invalid domain for user: ";

    private static final Logger logger = LoggerFactory.getLogger(DomainValidator.class);

    public void validate(String email, String domain) {
        if (!FT_DOMAIN.equals(domain)) {
            logger.warn(AUTHORIZATION_FAILURE_REASON + email);
            throw new InvalidDomainException();
        }
    }
}
