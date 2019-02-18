package com.ft.report.date;

import com.ft.asanaapi.model.CustomTask;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Predicate;

public interface DayPredicateFactory {
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    Predicate<CustomTask> create(LocalDate today);

    default String getNextDay(LocalDate day, DayOfWeek nextDayOfWeek) {
        return day.with(TemporalAdjusters.next(nextDayOfWeek)).format(dateFormat);
    }

    default LocalDate getNextDate(LocalDate day, DayOfWeek nextDayOfWeek) {
        return day.with(TemporalAdjusters.next(nextDayOfWeek));
    }
}
