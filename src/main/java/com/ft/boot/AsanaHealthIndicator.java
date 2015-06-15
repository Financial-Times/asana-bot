package com.ft.boot;

import com.ft.services.AsanaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import retrofit.client.Response;

@Component
public class AsanaHealthIndicator implements HealthIndicator {

    @Autowired
    private AsanaService asanaService;

    @Override
    public Health health() {
        Response response = asanaService.ping();
        if (response.getStatus() != 200) {
            return Health.down()
                    .withDetail("asanaStatus", response.getStatus()).withDetail("asanaResponse", response.getReason()).
                            build();
        }
        return Health.up().build();
    }

}
