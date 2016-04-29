package com.ft.tasks;

import com.asana.models.Task;
import com.ft.asanaapi.AsanaClientWrapper;
import com.ft.config.TaskBot;
import com.google.api.client.util.DateTime;
import com.joestelmach.natty.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("taskDueDateTaskRunner")
public class TaskDueDateTaskRunner implements TaskRunner {

    private static final Pattern TITLE_PATTERN = Pattern.compile("(.+)\\|(.*)");
    private static final Logger logger = LoggerFactory.getLogger(TaskDueDateTaskRunner.class);

    @Override
    public void run(final TaskBot taskBot) {
        AsanaClientWrapper client = taskBot.getClient();
        try {
            List<Task> tasks = client.getTasksByProject(taskBot.getProjectId());

            tasks.stream().forEach(task -> {
                Matcher matcher = TITLE_PATTERN.matcher(task.name);
                Map<String, Object> taskData = new HashMap<>();
                while (matcher.find()) {
                    taskData.put("name", matcher.group(1));
                    taskData.put("dueOn", parseDueDate(matcher.group(2)));
                    try {
                        client.updateTask(task, taskData);
                    } catch (IOException e) {
                        logger.error("error updating task", e);
                    }
                }
            });
        } catch (IOException e) {
            logger.error("error updating task", e);

        }

    }

    private DateTime parseDueDate(String date) {
        Parser parser = new Parser();
        Date parsedDate = parser.parse(date).get(0).getDates().get(0);
        return new DateTime(parsedDate);

    }


}
