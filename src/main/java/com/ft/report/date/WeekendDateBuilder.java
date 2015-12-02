package com.ft.report.date;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class WeekendDateBuilder implements DateBuilder {
    private static final DateTimeFormatter dayDateFormat = DateTimeFormatter.ofPattern("dd");
    private static final DateTimeFormatter dayMonthDateFormat = DateTimeFormatter.ofPattern("dd MMMM");
    private static final DateTimeFormatter longDateFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    protected String formatThisWeekend(LocalDate today) {
        LocalDate dayAgo = today.minusDays(1);
        String lastWeek = dayAgo.format(dayDateFormat);
        if (!dayAgo.getMonth().equals(today.getMonth())) {
            lastWeek = dayAgo.format(dayMonthDateFormat);
        }
        return lastWeek + " - " + today.format(longDateFormat);
    }
}
