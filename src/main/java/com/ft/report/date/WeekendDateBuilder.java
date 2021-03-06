package com.ft.report.date;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class WeekendDateBuilder implements DateBuilder {
    private static final DateTimeFormatter dayDateFormat = DateTimeFormatter.ofPattern("dd");
    private static final DateTimeFormatter dayMonthDateFormat = DateTimeFormatter.ofPattern("dd MMMM");
    private static final DateTimeFormatter longDateFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    protected String formatThisWeekend(final LocalDate today, final boolean isTwoWeeks) {
        if(isTwoWeeks) {
            return String.join(" & ", formatWeekends(today.minusDays(7)), formatWeekends(today));
        }
        return formatWeekends(today);
    }

    protected String formatThisWeekend(final LocalDate today) {
        return formatThisWeekend(today, false);
    }

    private String formatWeekends(final LocalDate today) {

        LocalDate dayAgo = today.minusDays(1);
        String yesterday = dayAgo.format(dayDateFormat);
        if (!dayAgo.getMonth().equals(today.getMonth())) {
            yesterday = dayAgo.format(dayMonthDateFormat);
        }
        return yesterday + " - " + today.format(longDateFormat);
    }
}
