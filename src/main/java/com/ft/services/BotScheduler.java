package com.ft.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BotScheduler {
    private static final int ONE_HOUR = 60 * 60_000;
    private static final int FIVE_MINUTES = 5 * 60_000;
    private static final int TWENTY_SEC = 20_000;

    private AsanaBotService asanaBotService;

    @Autowired
    public BotScheduler(AsanaBotService asanaBotService) {
        this.asanaBotService = asanaBotService;
    }

    @Scheduled(fixedRate = TWENTY_SEC)
    public void runTwentySecondsBots() {
        asanaBotService.runBots(TWENTY_SEC);
    }

    @Scheduled(fixedRate = ONE_HOUR)
    public void runOneHourBots() {
        asanaBotService.runBots(ONE_HOUR);
    }

    @Scheduled(fixedRate = FIVE_MINUTES)
    public void runFiveMinutesBots() {
        asanaBotService.runBots(FIVE_MINUTES);
    }
}
