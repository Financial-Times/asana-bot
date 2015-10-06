package com.ft.report.model;

import lombok.Data;

@Data
public class Project {
    private Long id;
    private String name;
    private Boolean primary = false;
}
