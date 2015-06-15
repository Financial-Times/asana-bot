package com.ft;

import com.ft.asanaapi.AsanaClient;
import com.ft.config.Config;
import com.ft.report.ReportGenerator;
import com.ft.report.ReportType;
import com.ft.report.SundayForMondayReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.security.oauth2.sso.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableOAuth2Sso
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

    @Autowired
    private SundayForMondayReportGenerator sundayForMondayReportGenerator;

    @Bean(name = "reportGenerators")
    public Map<ReportType, ReportGenerator> reportGenerators() {
        Map<ReportType, ReportGenerator> mapping = new HashMap<>();
        mapping.put(ReportType.SUNDAY_FOR_MONDAY, sundayForMondayReportGenerator);
        return mapping;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AsanaBot.class, args);
    }
}