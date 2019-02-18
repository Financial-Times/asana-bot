package com.ft.report.date;

import com.ft.asanaapi.model.CustomTask;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.function.Predicate;

public class ThisWeekPredicateFactory implements DayPredicateFactory {
    @Override
    public Predicate<CustomTask> create(LocalDate today) {
        LocalDate nextMonday = getNextDate(today, DayOfWeek.MONDAY);
        return rt -> LocalDate.parse(rt.getDue_on(), dateFormat).isBefore(nextMonday);
    }
}
