package com.ft.report.date;

import com.ft.report.model.ReportType;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class ReportDateBuilder {
    @Setter
    private Clock clock = Clock.systemUTC();

    private final Map<ReportType, DateBuilder> dateBuilders;

    public ReportDateBuilder() {
        dateBuilders = createDateBuilders();
    }

    private Map<ReportType, DateBuilder> createDateBuilders() {
        Map<ReportType, DateBuilder> dateBuilders = new HashMap<>();
        dateBuilders.put(ReportType.TODAY, new TodayDateBuilder());
        dateBuilders.put(ReportType.TOMORROW, new TomorrowDateBuilder());
        dateBuilders.put(ReportType.SUNDAY_FOR_MONDAY, new ThisSundayDateBuilder());
        dateBuilders.put(ReportType.THIS_WEEK, new ThisWeekendDateBuilder());
        dateBuilders.put(ReportType.NEXT_WEEK, new NextWeekendDateBuilder());
        dateBuilders.put(ReportType.TWO_WEEKS, new TwoWeekendDateBuilder());
        return dateBuilders;
    }

    public String buildReportDate(ReportType reportType) {
        LocalDate today = LocalDate.now(clock);
        DateBuilder dateBuilder = dateBuilders.get(reportType);
        return dateBuilder.build(today);
    }
}
