package com.ft.report.date;

import com.ft.report.model.ReportTask;

import java.time.LocalDate;
import java.util.function.Predicate;

public class DayAfterTomorrowPredicateFactory implements DayPredicateFactory {
    @Override
    public Predicate<ReportTask> create(LocalDate today) {
        String dayAfterTommorow = today.plusDays(2).format(dateFormat);
        return rt -> dayAfterTommorow.equals(rt.getDue_on());
    }
}
