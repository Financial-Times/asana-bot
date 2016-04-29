package com.ft;

import com.ft.asanaapi.AsanaClient;
import com.ft.config.Config;
import com.ft.tasks.TaskRunnerFactory;
import com.squareup.okhttp.OkHttpClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class AsanaBot {

    @Autowired
    private Config config;

    @Bean(name = "okHttpClient")
    public OkHttpClient getHttpClient() {
        final OkHttpClient httpClient = new OkHttpClient();
        httpClient.setReadTimeout(5, TimeUnit.MINUTES);
        httpClient.setConnectTimeout(5, TimeUnit.SECONDS);
        return httpClient;
    }

    @Bean(name = "graphicsAsanaClient")
    public AsanaClient getGraphicsAsanaClient() {
        return new AsanaClient(System.getenv("ASANA_GRAPHICS_KEY"), config, getHttpClient());
    }

    @Bean(name = "reportAsanaClient")
    public AsanaClient getReportAsanaClient() {
        return new AsanaClient(System.getenv("ASANA_REPORT_KEY"), config, getHttpClient());
    }

    @Bean
    public FactoryBean serviceLocatorFactoryBean() {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(TaskRunnerFactory.class);
        return factoryBean;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AsanaBot.class, args);
    }
}
