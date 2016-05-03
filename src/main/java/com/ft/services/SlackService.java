package com.ft.services;

import com.asana.models.Project;
import com.ft.monitoring.Change;
import com.ft.monitoring.ChangeType;
import com.ft.monitoring.ProjectChange;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "notify")
@Component
@Setter
public class SlackService {

    private RestTemplate restTemplate;
    private String slackWebHookUrl;

    public SlackService() {
        restTemplate = new RestTemplate();
    }

    public SlackService(RestTemplate restTemplate, String slackWebHookUrl){
        this.restTemplate = restTemplate;
        this.slackWebHookUrl = slackWebHookUrl;
    }

    private void notifySlack(Map<String, Object> payloadContent) throws IOException {
        URI uri = URI.create(slackWebHookUrl);
        restTemplate.postForLocation(uri, payloadContent);
    }

    public void notifyProjectChange(List<ProjectChange> projectChanges) throws Exception {

        if(projectChanges == null || projectChanges.isEmpty()){
            throw new RuntimeException("There are no changes to notify");
        }
        Map<String, Object> payloadContent = Maps.newHashMap();
        List<Map<String, Object>> attachments = Lists.newArrayList();
        projectChanges.stream().forEach(projectChange -> attachments.addAll(createSlackMessage(projectChange)));

        payloadContent.put("text", "Project Changed Alert <!channel>");
        payloadContent.put("attachments", attachments);

        notifySlack(payloadContent);
    }

    private List<Map<String, Object>> createSlackMessage(ProjectChange projectChange){
        List<Map<String, Object> > attachments = Lists.newArrayList();

        if(!projectChange.isProjectChanged()){
            throw new RuntimeException("There are no changes to notify");
        }
        List<Change> changes = projectChange.getChanges();
        for(Change change : changes) {

            Map<String, Object> attachmentsContent = Maps.newHashMap();

            List<Map<String, Object>> fields = Lists.newArrayList();
            Project project = projectChange.getProject() != null ? projectChange.getProject() : projectChange.getReferenceProject();
            String projectName = project.name;
            String projectId = project.id;
            String projectLink = "https://app.asana.com/0/" + projectId + "/" + projectId;

            ChangeType changeType = change.getType();

            switch (changeType) {
                case NAME:
                    attachmentsContent.put("text", "Project " + "<" + projectLink + "|" + projectName + ">" + " has changed name");
                    populateFieldsContent(fields, "new name", "old name", change);
                    break;
                case TEAM:
                    attachmentsContent.put("text", "Project " + "<" + projectLink + "|" + projectName + ">" + " has been moved into another team");
                    populateFieldsContent(fields, "new team", "old team", change);
                    break;
                case ARCHIVED:
                    attachmentsContent.put("text", "Project " + "<" + projectLink + "|" + projectName + ">" + " has been archived");
                    break;
                case NOT_FOUND:
                    attachmentsContent.put("text", "Project " + "<" + projectName + ">" + " could not be found.");
                    break;
            }
            attachmentsContent.put("fallback", "<http://asana.co.uk/>");
            attachmentsContent.put("color", "#D00000");
            attachmentsContent.put("fields", fields);
            attachments.add(attachmentsContent);
        }

        return attachments;
    }

    private void populateFieldsContent(List<Map<String, Object>> fields,String title1, String title2,
                                       Change change){
        Map<String, Object> fieldsContent1 = Maps.newHashMap();
        Map<String, Object> fieldsContent2 = Maps.newHashMap();
        fieldsContent1.put("title", title1);
        fieldsContent2.put("title", title2);
        fieldsContent1.put("value", change.getNewValue());
        fieldsContent2.put("value", change.getOldValue());
        fieldsContent1.put("color", "36a64f");
        fieldsContent2.put("color", "36a64f");
        fieldsContent1.put("short", "false");
        fieldsContent2.put("short", "false");
        fields.add(fieldsContent1);
        fields.add(fieldsContent2);
    }

}
