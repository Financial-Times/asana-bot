package com.ft.services;

import com.ft.asanaapi.AsanaClientWrapper;
import com.ft.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AsanaService {

     private AsanaClientWrapper defaultAsanaClientWrapper;
     private Config config;

    @Autowired
    public AsanaService(AsanaClientWrapper defaultAsanaClientWrapper, Config config) {
        this.defaultAsanaClientWrapper = defaultAsanaClientWrapper;
        this.config = config;
    }


    public void ping() throws IOException {
        defaultAsanaClientWrapper.getWorkspace(config.getWorkspace());
    }


}
