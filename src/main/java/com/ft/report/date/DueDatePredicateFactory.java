package com.ft.report.date;

import com.ft.report.model.ReportTask;
import com.ft.report.model.ReportType;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@Component
public class DueDatePredicateFactory {
    @Setter
    private Clock clock = Clock.systemUTC();

    private final Map<ReportType, DayPredicateFactory> dayPredicateFactories;

    public DueDatePredicateFactory() {
        dayPredicateFactories = createDayPredicateFactories();
    }

    private Map<ReportType, DayPredicateFactory> createDayPredicateFactories() {
        Map<ReportType, DayPredicateFactory> dayPredicateFactories = new HashMap<>();
        dayPredicateFactories.put(ReportType.TODAY, new TodayPredicateFactory());
        dayPredicateFactories.put(ReportType.TOMORROW, new TomorrowPredicateFactory());
        dayPredicateFactories.put(ReportType.DAY_AFTER_TOMORROW, new DayAfterTomorrowPredicateFactory());
        dayPredicateFactories.put(ReportType.SUNDAY_FOR_MONDAY, new SundayPredicateFactory());
        dayPredicateFactories.put(ReportType.THIS_WEEK, new ThisWeekPredicateFactory());
        dayPredicateFactories.put(ReportType.NEXT_WEEK, new NextWeekPredicateFactory());
        dayPredicateFactories.put(ReportType.TWO_WEEKS, new TwoWeeksPredicateFactory());
        return dayPredicateFactories;
    }

    public Predicate<ReportTask> create(ReportType reportType) {
        LocalDate today = LocalDate.now(clock);
        DayPredicateFactory dayPredicateFactory = dayPredicateFactories.get(reportType);
        return dayPredicateFactory.create(today);
    }
}
