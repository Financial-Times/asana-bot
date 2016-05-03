package com.ft.services;

import com.ft.config.Config;
import com.ft.tasks.TaskRunner;
import com.ft.tasks.TaskRunnerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AsanaBotService {

    private Config config;
    private TaskRunnerFactory taskRunnerFactory;

    @Autowired
    public AsanaBotService(Config config, TaskRunnerFactory taskRunnerFactory) {
        this.config = config;
        this.taskRunnerFactory = taskRunnerFactory;
    }

    public void runBots(Integer runInterval) {
        config.getBots().parallelStream()
                .filter(bot -> bot.getRunInterval().equals(runInterval) )
                .forEach(bot -> {
            TaskRunner taskRunner = taskRunnerFactory.getTaskRunner(bot.getRunnerBean());
            taskRunner.run(bot);
        });
    }

}
