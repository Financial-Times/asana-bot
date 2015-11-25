package com.ft.report;

import com.ft.report.model.ReportTask;
import com.ft.report.model.ReportType;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Predicate;

@Component
public class DueDatePredicateFactory {

    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Setter
    private Clock clock = Clock.systemUTC();

    public Predicate<ReportTask> create(ReportType reportType) {
        LocalDate today = LocalDate.now(clock);
        // TODO: replace if block with polymorphism
        if (reportType == ReportType.SUNDAY_FOR_MONDAY) {
            return createSundayForMondayPredicate(today);
        }
        if (reportType == ReportType.TOMORROW) {
            return createTomorrowPredicate(today);
        }
        if (reportType == ReportType.TODAY) {
            return createTodayPredicate(today);
        }
        if (reportType == ReportType.THIS_WEEK) {
            return createNextWeekEndPredicate(today);
        }
        if (reportType == ReportType.NEXT_WEEK) {
            return createNextWeekPredicate(today);
        }

        return createAllMatchPredicate();
    }

    private Predicate<ReportTask> createSundayForMondayPredicate(LocalDate today) {
        String sunday = getNextDay(today, DayOfWeek.SUNDAY);
        return rt -> sunday.equals(rt.getDue_on());
    }

    private Predicate<ReportTask> createTomorrowPredicate(LocalDate today) {
        String tomorrow = today.plusDays(1).format(dateFormat);
        return rt -> tomorrow.equals(rt.getDue_on());
    }

    private Predicate<ReportTask> createTodayPredicate(LocalDate today) {
        String todayAsString = today.format(dateFormat);
        return rt -> todayAsString.equals(rt.getDue_on());
    }

    private Predicate<ReportTask> createNextWeekPredicate(LocalDate today) {
        LocalDate nextWeekMonday = getNextDate(today.plusDays(7), DayOfWeek.MONDAY);
        LocalDate nextMonday = getNextDate(today, DayOfWeek.MONDAY);
        return rt -> LocalDate.parse(rt.getDue_on(), dateFormat).isBefore(nextWeekMonday)
                && LocalDate.parse(rt.getDue_on(), dateFormat).isAfter(nextMonday);
    }

    private Predicate<ReportTask> createNextWeekEndPredicate(LocalDate today) {
        LocalDate nextMonday= getNextDate(today, DayOfWeek.MONDAY);
        return rt -> LocalDate.parse(rt.getDue_on(), dateFormat).isBefore(nextMonday);
    }

    private Predicate<ReportTask> createAllMatchPredicate() {
        return rt -> true;
    }

    private String getNextDay(LocalDate day, DayOfWeek nextDayOfWeek) {
        return day.with(TemporalAdjusters.next(nextDayOfWeek)).format(dateFormat);
    }

    private LocalDate getNextDate(LocalDate day, DayOfWeek nextDayOfWeek) {
        return day.with(TemporalAdjusters.next(nextDayOfWeek));
    }
}
