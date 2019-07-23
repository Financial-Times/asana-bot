package com.ft.report.date;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DayAfterTomorrowDateBuilder implements DateBuilder {
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String build(LocalDate date) {
        return date.plusDays(2).format(dateFormat);
    }
}
