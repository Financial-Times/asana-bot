package com.ft.backup.model;

import com.ft.asanaapi.model.ProjectInfo;
import lombok.Data;

import java.util.List;

@Data
public class ProjectsData {
    private List<ProjectInfo> data;
}
