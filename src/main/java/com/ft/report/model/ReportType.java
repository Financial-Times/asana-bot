package com.ft.report.model;

import org.springframework.util.StringUtils;
import java.util.StringJoiner;

public enum ReportType {
    SUNDAY_FOR_MONDAY,
    TOMORROW,
    TODAY;

    public String format() {
        String[] tokens = this.name().toLowerCase().split("_");
        StringJoiner joiner = new StringJoiner(" ");
        for (String token : tokens) {
            joiner.add(StringUtils.capitalize((token)));
        }
        return joiner.toString();
    }
}
