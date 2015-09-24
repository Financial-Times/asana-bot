package com.ft.asanaapi;

import com.ft.asanaapi.model.Tag;
import com.ft.asanaapi.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TagCleanupService {
    @Autowired AsanaClient graphicsAsanaClient;

    public CleanupReport cleanup() {
        CleanupReport cleanupReport = new CleanupReport();

        Map<String, List<Tag>> allTags = getAllTags();
        cleanupReport.setDuplicatedTags(getDuplicatedTags(allTags));
        if (cleanupReport.getDuplicatedTags() == null || cleanupReport.getDuplicatedTags().isEmpty()) {
            cleanupReport.setCleanupNeeded(false);
        } else {
            findTasksAndReplaceTags(cleanupReport);
        }

        return  cleanupReport;
    }

    public Map<String, List<Tag>> getAllTags() {
        List<Tag> allTags = graphicsAsanaClient.getAllTags();
        Map<String, List<Tag>> tagsByName = new HashMap<>();
        for (Tag tag: allTags) {
            List<Tag> tagsWithSameName = tagsByName.getOrDefault(tag.getName(), new ArrayList<>());
            tagsWithSameName.add(tag);
            tagsByName.put(tag.getName(), tagsWithSameName);
        }
        return tagsByName;
    }

    public Map<String, List<Tag>> getDuplicatedTags(Map<String, List<Tag>> tagsByName) {
        Map<String, List<Tag>> duplicatedTagsByName = new HashMap<>();
        for (String tagName: tagsByName.keySet()) {
            List<Tag> tagsWithSameName = tagsByName.getOrDefault(tagName, new ArrayList<>());
            if (tagsWithSameName.size() > 1) {
                duplicatedTagsByName.put(tagName, tagsWithSameName);
            }
        }
        return duplicatedTagsByName;
    }

    public void findTasksAndReplaceTags(CleanupReport cleanupReport) {
        Map<String, List<Tag>> tags = cleanupReport.getDuplicatedTags();
        for (String tagName: tags.keySet()) {
            Tag firstTag = tags.get(tagName).get(0);
            for (Tag tag: tags.get(tagName)) {
                List<Task> tasks = findTasksByTag(tag);
                System.out.println("Deduplicating tag:" + tagName);
                deduplicateTags(cleanupReport, tasks, firstTag);
            }
            break;
            //tryToSleep();
        }
    }


    public List<Task> findTasksByTag(Tag tag) {
        return graphicsAsanaClient.getTasksByTag(tag);
    }

    private void deduplicateTags(CleanupReport cleanupReport, List<Task> tasks, Tag tagToKeep) {
        tasks.stream().forEach(task -> {
            List<Tag> currentTags = task.getTags();
            currentTags.stream().forEach(currentTag -> {
                if (currentTag.getName().equals(tagToKeep.getName())) {
                    System.out.println("Removing tag " + currentTag.getId() + " from " + task.getName());
                    if (!currentTag.equals(tagToKeep)) {
                        List<Tag> tagsToRemove = cleanupReport.getTagsToRemove().getOrDefault(tagToKeep, new ArrayList<>());
                        tagsToRemove.add(currentTag);
                        cleanupReport.getTagsToRemove().put(tagToKeep, tagsToRemove);
                    }
                    graphicsAsanaClient.removeTagFromTask(task, currentTag);
                }
            });
            System.out.println("Adding tag " + tagToKeep.getId() + " to " + task.getName());
            graphicsAsanaClient.addTagToTask(task, tagToKeep);
            if (tasks.indexOf(task) % 35 == 34) {
                tryToSleep();
            }
        });
    }

    private void tryToSleep() {
        try {
            System.out.println("Going to sleep to avoid 429 (rate limit exceeded response from asana ");
            Thread.sleep(60_000L);
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted" + e.getMessage());
        }
    }
}
