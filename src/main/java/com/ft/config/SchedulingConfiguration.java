package com.ft.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Profile("scheduling")
@Configuration
@EnableScheduling
@EnableAsync
public class SchedulingConfiguration {
}
