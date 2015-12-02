package com.ft.report.date;

import com.ft.report.model.ReportTask;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.function.Predicate;

public class SundayPredicateFactory implements DayPredicateFactory {
    @Override
    public Predicate<ReportTask> create(LocalDate today) {
        String sunday = getNextDay(today, DayOfWeek.SUNDAY);
        return rt -> sunday.equals(rt.getDue_on());
    }
}
