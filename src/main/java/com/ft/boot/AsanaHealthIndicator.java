package com.ft.boot;

import com.ft.services.AsanaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import retrofit.RetrofitError;
import retrofit.client.Response;

@Component
public class AsanaHealthIndicator implements HealthIndicator {

    @Autowired
    private AsanaService asanaService;

    @Override
    public Health health() {
        try {
            asanaService.ping();
        } catch(RetrofitError error) {
            Response response = error.getResponse();
            return buildDownHealth(response);
        }
        return Health.up().build();
    }

    private Health buildDownHealth(Response response) {
        return Health.down()
                .withDetail("asanaStatus", response.getStatus())
                .withDetail("asanaResponse", response.getReason())
                .build();
    }

}
