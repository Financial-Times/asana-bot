package com.ft.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.joelinn.asana.Asana;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BotScheduler {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    AsanaService asanaService;

    @Scheduled(fixedRate = 5000)
    public void pictureBot() {

        System.out.println(asanaService.getAssignedTasks());

        asanaService.addAssignedTasksToPictures();

        System.out.println("Picturebot time is now " + dateFormat.format(new Date()));
    }

    @Scheduled(fixedRate = 5000)
    public void graphicsBot(){
        //TODO
    }
}
