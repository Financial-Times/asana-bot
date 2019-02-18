package com.ft.report.date;

import com.ft.asanaapi.model.CustomTask;

import java.time.LocalDate;
import java.util.function.Predicate;

public class TomorrowPredicateFactory implements DayPredicateFactory {
    @Override
    public Predicate<CustomTask> create(LocalDate today) {
        String tomorrow = today.plusDays(1).format(dateFormat);
        return rt -> tomorrow.equals(rt.getDue_on());
    }
}
