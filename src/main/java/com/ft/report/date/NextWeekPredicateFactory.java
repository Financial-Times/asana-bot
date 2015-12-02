package com.ft.report.date;

import com.ft.report.model.ReportTask;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.function.Predicate;

public class NextWeekPredicateFactory implements DayPredicateFactory {
    @Override
    public Predicate<ReportTask> create(LocalDate today) {
        LocalDate nextWeekMonday = getNextDate(today.plusDays(7), DayOfWeek.MONDAY);
        LocalDate nextMonday = getNextDate(today, DayOfWeek.MONDAY);
        return rt -> LocalDate.parse(rt.getDue_on(), dateFormat).isBefore(nextWeekMonday)
                && LocalDate.parse(rt.getDue_on(), dateFormat).isAfter(nextMonday);
    }
}
