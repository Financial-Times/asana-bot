package com.ft.asanaapi;

import com.ft.asanaapi.model.Tag;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CleanupReport {

    private boolean cleanupNeeded = true;
    private Map<String, List<Tag>> duplicatedTags = new HashMap<>();
    private Map<Tag, List<Tag>> tagsToRemove = new HashMap<>();

    @Override
    public String toString() {
        if (!cleanupNeeded) {
            return "All OK. No duplicate tags found.";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Found ");
        stringBuilder.append(duplicatedTags.size());
        stringBuilder.append(" duplicated tags");
        stringBuilder.append("\nYou need to manually delete unassigned tags\n\n");
        String asanaBaseUrl = "https://app.asana.com/0/";
        for (Tag tagToKeep : tagsToRemove.keySet()) {
            stringBuilder.append("\nTags with name: ");
            stringBuilder.append(tagToKeep.getName());
            stringBuilder.append("\nTag to keep:\n");
            stringBuilder.append(tagToKeep.getId());
            stringBuilder.append("\nList of tags to delete:\n");
            for (Tag tagToDelete: tagsToRemove.get(tagToKeep)) {
                stringBuilder.append(asanaBaseUrl);
                stringBuilder.append(tagToDelete.getId());
                stringBuilder.append("/");
                stringBuilder.append(tagToDelete.getId());
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
