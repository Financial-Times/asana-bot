package com.ft.monitoring;

import com.ft.asanaapi.model.ProjectInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectChange {

    private ProjectInfo project;
    private List<ChangeType> changeTypes;

    public ProjectChange() {
        changeTypes = new ArrayList<>();
    }

    public void addChangeType(ChangeType changeType) {
        changeTypes.add(changeType);
    }
}
