package com.ft.sparkapi;

import com.ft.asanaapi.model.CustomTask;
import com.ft.sparkapi.graphqlQuery.ArticleCreate;
import com.google.common.collect.ImmutableMap;
import io.aexp.nodes.graphql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class SparkClient {

    private static final Logger logger = LoggerFactory.getLogger(SparkClient.class);

    @Value("${spark.desks.FTfm}")
    private String deskId;
    @Value("${spark.apiKey}")
    private String sparkApiKey;
    @Value("${spark.url}")
    private String sparkUrl;

    private GraphQLTemplate graphQLTemplate = new GraphQLTemplate();

    @SuppressWarnings("unchecked")
    public GraphQLResponseEntity<ArticleCreate> createArticleInSpark(String uuid, CustomTask task) {

        try {
            InputObject article = new InputObject.Builder<>()
                    .put("uuid", uuid)
                    .put("title", task.getName())
                    .put("author", "Asana User")
                    .put("desk", deskId)
                    .put("plannedPublishDate", dueDateStartOfDay(task.getDue_on())) //2019-02-15T00:00:00.000Z
                    .build();

            GraphQLRequestEntity requestEntity = GraphQLRequestEntity.Builder()
                    .url(sparkUrl)
                    .headers(ImmutableMap.of("x-api-key", sparkApiKey))
                    .arguments(new Arguments("articleCreate", new Argument("record", article)))
                    .request(ArticleCreate.class)
                    .build();

            return graphQLTemplate.mutate(requestEntity, ArticleCreate.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    private static String dueDateStartOfDay(final String dueDate) {
        String date = dueDate == null ? LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) : dueDate;
        return String.format("%sT00:00:00.000Z", date);
    }
}
