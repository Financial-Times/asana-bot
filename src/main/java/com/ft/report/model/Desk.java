package com.ft.report.model;

import lombok.Data;

import java.util.List;

@Data
public class Desk {
    private String projectId;
    private boolean groupTags = false;
    private List<String> premiumTags;
}
