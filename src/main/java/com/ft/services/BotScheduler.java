package com.ft.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BotScheduler {

    Logger logger = LoggerFactory.getLogger("com.ft.services.BotScheduler");

    @Autowired
    AsanaService asanaService;

    @Scheduled(fixedRate = 20000)
    public void graphicsBot() {
        logger.info("Graphicsbot grabbing tasks for project");

        asanaService.addGraphicsProjectToGraphicsBotAssignedTasks();

        logger.info("Graphicsbot finished processing tasks for project");
    }

    @Scheduled(fixedRate = 20000)
    public void picturesBot(){
        logger.info("Picturesbot grabbing tasks for project");

        asanaService.addPicturesProjectToPicturesBotAssignedTasks();

        logger.info("Picturesbot finished processing tasks for project");
    }
}
