package com.ft.report.date;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class ThisWeekendDateBuilder extends WeekendDateBuilder {

    public String build(LocalDate date) {
        LocalDate sunday = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return formatThisWeekend(sunday);
    }
}
