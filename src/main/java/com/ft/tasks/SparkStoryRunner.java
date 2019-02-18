package com.ft.tasks;

import com.ft.asanaapi.AsanaClientWrapper;
import com.ft.asanaapi.model.CustomField;
import com.ft.asanaapi.model.CustomTask;
import com.ft.config.TaskBot;
import com.ft.sparkapi.SparkClient;
import com.google.common.collect.ImmutableMap;
import io.aexp.nodes.graphql.GraphQLResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Component("sparkStoryRunner")
public class SparkStoryRunner implements TaskRunner {

    private static final Logger logger = LoggerFactory.getLogger(SparkStoryRunner.class);
    @Autowired
    private SparkClient sparkClient;

    public void run(final TaskBot taskBot) {
        AsanaClientWrapper client = taskBot.getClient();
        try {
            List<CustomTask> tasks = client.getCustomTasks(taskBot.getProjectId());

            tasks.stream().filter(SparkStoryRunner::isReadyForSpark).forEach((CustomTask task) -> {
                String uuid = UUID.randomUUID().toString();
                GraphQLResponseEntity responseEntity = sparkClient.createArticleInSpark(uuid, task);
                if (responseEntity.getErrors() == null || responseEntity.getErrors().length == 0) {
                    final String customFieldID = getCustomFieldID("ORIGINAL UUID", task.getCustom_fields());
                    try {
                        final Map<String, Object> data = ImmutableMap.of("custom_fields", ImmutableMap.of(customFieldID, uuid));
                        client.updateTask(task, data);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            });
        } catch (IOException e) {
            logger.error("error updating task", e);

        }

    }


    /**
     * A task are ready for Spark if it has a name property, an original uuid customField and it is older than five minus
     *
     * @param task the task
     * @return a boolean
     */
    private static boolean isReadyForSpark(CustomTask task) {
        Instant fiveMinsAgo = Instant.now().minus(1, ChronoUnit.MINUTES);
        return task.getName() != null
                && task.getName().matches(".*TEST.*")
                && Instant.parse(task.getCreated_at()).isBefore(fiveMinsAgo)
                && task.getCustom_fields()
                        .stream()
                        .noneMatch(d -> d.getName().equals("ORIGINAL UUID") && d.getText_value() != null);
    }

    /**
     * @param fieldName the name of the custom field to extract
     * @param fields    the custom fields
     * @return Customfield Id
     */

    private static String getCustomFieldID(final String fieldName, final List<CustomField> fields) {
        CustomField customField = fields
                .stream()
                .filter(f -> f.getName().equals(fieldName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("custom filed missing in asana"));


        return customField.getId();

    }

}
