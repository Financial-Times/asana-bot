package com.ft.asanaapi.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "report")
@Getter
@Setter
@NoArgsConstructor
public class DomainValidator {

    private static final String AUTHORIZATION_FAILURE_REASON = "Authorization failed due to invalid domain for user: ";
    private static final Logger logger = LoggerFactory.getLogger(DomainValidator.class);

    private String hostDomain;

    public void validate(String email, String domain) {
        if (!hostDomain.equals(domain)) {
            logger.warn(AUTHORIZATION_FAILURE_REASON + email);
            throw new InvalidDomainException();
        }
    }
}
