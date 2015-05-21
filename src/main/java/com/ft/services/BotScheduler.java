package com.ft.services;

import com.ft.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("production")
public class BotScheduler {

    @Autowired
    private AsanaService asanaService;

    @Scheduled(fixedRate = 20000)
    public void graphicsBot() {
        asanaService.addGraphicsProjectToGraphicsBotAssignedTasks();
    }

    @Scheduled(fixedRate = 20000)
    public void picturesBot(){
        asanaService.addPicturesProjectToPicturesBotAssignedTasks();
    }
}
