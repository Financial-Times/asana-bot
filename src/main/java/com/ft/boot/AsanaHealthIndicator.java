package com.ft.boot;

import com.asana.errors.AsanaError;
import com.ft.asanaapi.AsanaClientWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AsanaHealthIndicator implements HealthIndicator {

    private AsanaClientWrapper asanaClientWrapper;

    @Autowired
    public AsanaHealthIndicator(AsanaClientWrapper defaultAsanaClientWrapper) {
        this.asanaClientWrapper = defaultAsanaClientWrapper;
    }

    @Override
    public Health health() {
        try {
            asanaClientWrapper.getWorkspace();
        } catch(IOException error) {
            if (error instanceof AsanaError) {
                return buildDownHealth( ((AsanaError)error).status, error.getMessage());
            }
            return buildDownHealth(500, error.getMessage());
        }
        return Health.up().build();
    }

    private Health buildDownHealth(int status, String response) {
        return Health.down()
                .withDetail("asanaStatus", status)
                .withDetail("asanaResponse", response)
                .build();
    }

}
