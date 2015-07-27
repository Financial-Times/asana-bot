package com.ft.backup.model;

import com.ft.asanaapi.model.ResultPage;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class BackupTasksData {
    private List<BackupTask> data;
    @SerializedName("next_page") private ResultPage nextPage;
}
