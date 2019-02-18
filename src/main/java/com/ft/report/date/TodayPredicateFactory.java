package com.ft.report.date;

import com.ft.asanaapi.model.CustomTask;

import java.time.LocalDate;
import java.util.function.Predicate;

public class TodayPredicateFactory implements DayPredicateFactory {
    @Override
    public Predicate<CustomTask> create(LocalDate today) {
        String todayAsString = today.format(dateFormat);
        return rt -> todayAsString.equals(rt.getDue_on());
    }
}
