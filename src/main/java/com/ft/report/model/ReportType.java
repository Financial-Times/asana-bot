package com.ft.report.model;

import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public enum ReportType {
    SUNDAY_FOR_MONDAY(ReportCategory.WEEKDAY),
    TOMORROW(ReportCategory.WEEKDAY),
    TODAY(ReportCategory.WEEKDAY),
    THIS_WEEK(ReportCategory.WEEKEND),
    NEXT_WEEK(ReportCategory.WEEKEND);

    @Setter
    private final List<ReportCategory> category;

    ReportType(ReportCategory ... category) {
        this.category = Arrays.asList(category);
    }

    public String format() {
        String[] tokens = this.name().toLowerCase().split("_");
        StringJoiner joiner = new StringJoiner(" ");
        for (String token : tokens) {
            joiner.add(StringUtils.capitalize((token)));
        }
        return joiner.toString();
    }

    public List<String> getCategory() {
        return category.stream().map(Enum::name).collect(Collectors.toList());
    }
}
