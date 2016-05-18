package com.ft.services;

import com.ft.asanaapi.AsanaClientWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AsanaService {
     private AsanaClientWrapper defaultAsanaClientWrapper;

    @Autowired
    public AsanaService(AsanaClientWrapper defaultAsanaClientWrapper) {
        this.defaultAsanaClientWrapper = defaultAsanaClientWrapper;
    }


    public void ping() throws IOException {
        defaultAsanaClientWrapper.getWorkspace();
    }


}
