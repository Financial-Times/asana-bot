package com.ft.asanaapi.model;

import lombok.Data;

/**
 * The containing class for data returned from asana api requests
 * querying for project info.
 */
@Data
public class ProjectData {
    private ProjectInfo data;
}
