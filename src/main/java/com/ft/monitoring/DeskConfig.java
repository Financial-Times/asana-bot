package com.ft.monitoring;

import com.ft.report.model.Desk;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "report")
public class DeskConfig {

    private Map<String, Desk> desks;
}
