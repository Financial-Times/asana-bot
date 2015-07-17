package com.ft.changes;

import java.util.List;

import com.ft.monitoring.AsanaChangesService;
import com.ft.monitoring.ProjectChange;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("web")
@RestController
@RequestMapping("/changes")
public class ChangesController {
    @Autowired private AsanaChangesService asanaChangesService;

    @RequestMapping("/projects")
    public List<ProjectChange> projectChanges(){
        List<ProjectChange> projectChanges = asanaChangesService.getChanges();
        return projectChanges == null ? Lists.newArrayList() : projectChanges ;
    }

}