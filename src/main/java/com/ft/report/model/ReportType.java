package com.ft.report.model;

import org.springframework.util.StringUtils;

import java.util.*;

public enum ReportType {
    SUNDAY_FOR_MONDAY(ReportCategory.WEEKDAY),
    TOMORROW(ReportCategory.WEEKDAY),
    TODAY(ReportCategory.WEEKDAY),
    THIS_WEEK(ReportCategory.WEEKEND),
    NEXT_WEEK(ReportCategory.WEEKEND),
    TWO_WEEKS(ReportCategory.WEEKEND);

    private final ReportCategory category;

    ReportType(ReportCategory category) {
        this.category = category;
    }

    public String format() {
        String[] tokens = this.name().toLowerCase().split("_");
        StringJoiner joiner = new StringJoiner(" ");
        for (String token : tokens) {
            joiner.add(StringUtils.capitalize((token)));
        }
        return joiner.toString();
    }

    public String formatAndAppendCategory() {
        String formatted = format();
        String suffix = formatted.endsWith("s") ? "' conference report " :"'s conference report ";
        if (category == ReportCategory.WEEKEND) {
            suffix = formatted.endsWith("s") ? "' plan " : "'s plan ";
        }
        return formatted + suffix;
    }

    public ReportCategory getCategory() {
        return category;
    }

    public static Map<String, List<Map<String, String>>> getReportTypesByCategories() {
        Map<String, List<Map<String, String>>> reportTypes = new LinkedHashMap<>();
        for (ReportType reportType : ReportType.values()) {
            List<Map<String, String>> reportTypeProperties = reportTypes.getOrDefault(reportType.category.name(), new ArrayList<>());
            Map<String, String> reportTypeProperty = new LinkedHashMap<>();
            reportTypeProperty.put("id", reportType.name());
            reportTypeProperty.put("name", reportType.format());
            reportTypeProperties.add(reportTypeProperty);

            reportTypes.put(reportType.category.name(), reportTypeProperties);
        }
        return reportTypes;
    }
}
