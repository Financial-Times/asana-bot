package com.ft.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    private String id;
    private String name;
    private Boolean primary = true;

    public Project(Long id) {
        this.id = String.valueOf(id);
    }
}
