package com.ft;

import com.ft.asanaapi.AsanaClient;
import com.ft.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@EnableScheduling
public class AsanaBot {

    @Autowired
    private Config config;


    @Bean(name = "graphicsAsanaClient")
    public AsanaClient getGraphicsAsanaClient() {
        return new AsanaClient(System.getenv("ASANA_GRAPHICS_KEY"), config);
    }

    @Bean(name = "picturesAsanaClient")
    public AsanaClient getPicturesAsanaClient() {
        return new AsanaClient(System.getenv("ASANA_PICTURES_KEY"), config);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AsanaBot.class, args);
    }
}