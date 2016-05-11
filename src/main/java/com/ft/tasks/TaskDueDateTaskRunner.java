package com.ft.tasks;

import com.asana.models.Task;
import com.ft.asanaapi.AsanaClientWrapper;
import com.ft.config.TaskBot;
import com.joestelmach.natty.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("taskDueDateTaskRunner")
public class TaskDueDateTaskRunner implements TaskRunner {

    private static final Pattern TITLE_PATTERN = Pattern.compile("(.+)\\|(.*)");
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("(.+):(.*)");
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
                    final String taskName = matcher.group(1);
                    final String taskDueDate = matcher.group(2);
                    final String dueDateField = TIMESTAMP_PATTERN.matcher(taskDueDate).matches() ? "due_at" : "due_on";
                    taskData.put("name", taskName);
                    taskData.put(dueDateField, parseDueDate(taskDueDate));
                    try {
                        client.updateTask(task, taskData);
                        logger.info("Successfully updated task: {} due date to {}.", task.id, taskDueDate);
                    } catch (IOException e) {
                        logger.error("error updating task", e);
                    }
                }
            });
        } catch (IOException e) {
            logger.error("error updating task", e);

        }

    }

    private String parseDueDate(String date) {
        Parser parser = new Parser();
        Date parsedDate = parser.parse(date).get(0).getDates().get(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return simpleDateFormat.format(parsedDate);

    }

}
