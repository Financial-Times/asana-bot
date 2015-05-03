package com.ft;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration
@SpringBootApplication
@EnableScheduling
public class AsanaBot {

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }

    @RequestMapping("/andrew")
    String andrew() {
        return "Hello Andrew!";
    }

    @RequestMapping("/stuart")
    String stuart() {
        return "Hello Stuart from Heroku via Circle CI deployment!!";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AsanaBot.class, args);
    }
}