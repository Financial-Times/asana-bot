package com.ft.services;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ft.monitoring.Change;
import com.ft.monitoring.ChangeType;
import com.ft.monitoring.ProjectChange;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ConfigurationProperties(prefix = "notify")
@Component
@Setter
public class SlackService {

    private RestTemplate restTemplate;
    private String slackWebHookUrl;

    public SlackService() {
        restTemplate = new RestTemplate();
    }

    protected SlackService(RestTemplate restTemplate, String slackWebHookUrl){
        this.restTemplate = restTemplate;
        this.slackWebHookUrl = slackWebHookUrl;
    }

    private void notifySlack(HashMap payloadContent) throws IOException {
        URI uri = URI.create(slackWebHookUrl);
        restTemplate.postForLocation(uri, payloadContent);
    }

    public void notifyProjectChange(List<ProjectChange> projectChanges) throws Exception {

        if(projectChanges == null || projectChanges.isEmpty()){
            throw new RuntimeException("There are no changes to notify");
        }
        HashMap<String, Object> payloadContent = Maps.newHashMap();
        List<Map<String, Object>> attachments = Lists.newArrayList();
        projectChanges.stream().forEach(projectChange -> attachments.addAll(createSlackMessage(projectChange)));

        payloadContent.put("text", "Project Changed Alert <!channel>");
        payloadContent.put("attachments", attachments);

        notifySlack(payloadContent);
    }

    private List<Map<String, Object>> createSlackMessage(ProjectChange projectChange){
        List<Map<String, Object> > attachments = Lists.newArrayList();

        List<Change> changes = projectChange.getChanges();
        if(changes == null || changes.isEmpty()){
            throw new RuntimeException("There are no changes to notify");
        }
        for(Change change : changes) {

            Map<String, Object> attachmentsContent = Maps.newHashMap();
            Map<String, Object> fieldsContent1 = Maps.newHashMap();
            Map<String, Object> fieldsContent2 = Maps.newHashMap();
            List<Map<String, Object>> fields = Lists.newArrayList();
            String projectName = projectChange.getProject().getName();
            String projectId = projectChange.getProject().getId();
            String projectLink = "https://app.asana.com/0/" + projectId + "/" + projectId;

            if(ChangeType.NAME.equals(change.getType()) || ChangeType.TEAM.equals(change.getType()) ){
                if(ChangeType.NAME.equals(change.getType())){
                    attachmentsContent.put("text", "Project " + "<" + projectLink + "|" + projectName + ">" + " has changed name");
                    fieldsContent1.put("title", "new name");
                    fieldsContent2.put("title", "old name");

                }
                else if(ChangeType.TEAM.equals(change.getType())) {
                    attachmentsContent.put("text", "Project " + "<" + projectLink + "|" + projectName + ">" +  " has been moved into another team");
                    fieldsContent1.put("title", "new team");
                    fieldsContent2.put("title", "old team");

                }
                fieldsContent1.put("value", change.getOldValue());
                fieldsContent2.put("value", change.getNewValue());
                fieldsContent1.put("color", "36a64f");
                fieldsContent2.put("color", "36a64f");
                fieldsContent1.put("short", "false");
                fieldsContent2.put("short", "false");
                fields.add(fieldsContent1);
                fields.add(fieldsContent2);

            }
            else if(ChangeType.ARCHIVED.equals(change.getType())){
                attachmentsContent.put("text",  "Project " + "<" + projectLink + "|" + projectName + ">" + " has been archived");
            }
            attachmentsContent.put("fallback", "<http://asana.co.uk/>");
            attachmentsContent.put("color", "#D00000");
            attachmentsContent.put("fields", fields);
            attachments.add(attachmentsContent);
        }

        return attachments;
    }

}
