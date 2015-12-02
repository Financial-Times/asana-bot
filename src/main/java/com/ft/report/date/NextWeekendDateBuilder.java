package com.ft.report.date;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class NextWeekendDateBuilder extends WeekendDateBuilder {
    public String build(LocalDate date) {
        LocalDate sunday = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).plusDays(7);
        return formatThisWeekend(sunday);
    }
}
