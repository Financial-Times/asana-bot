package com.ft.report.model;


import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum ImportantTag {
    LEVEL_1("Level 1"), CONFERENCE("Conference");

    @Getter
    private final String value;

    ImportantTag(String value) {
        this.value = value;
    }

    public static List<ImportantTag> asList() {
        return Arrays.asList(ImportantTag.values());
    }
}
