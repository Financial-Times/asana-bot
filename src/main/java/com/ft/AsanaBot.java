package com.ft;

import com.asana.Client;
import com.ft.asanaapi.AsanaClientWrapper;
import com.ft.config.Config;
import com.ft.tasks.TaskRunnerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AsanaBot {

    @Autowired
    private Config config;

    @Bean(name = "defaultAsanaClientWrapper")
    public AsanaClientWrapper getDefaultAsanaClientWrapper() {
        Client client = Client.accessToken(System.getenv("ASANA_GRAPHICS_KEY"));
        return new AsanaClientWrapper(client, config.getWorkspace());
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
