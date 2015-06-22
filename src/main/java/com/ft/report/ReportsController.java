package com.ft.report;

import com.ft.report.model.Criteria;
import com.ft.report.model.Report;
import com.ft.report.model.ReportType;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Profile("web")
@Controller
@RequestMapping("/")
public class ReportsController {

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

    @Setter private Clock clock = Clock.systemUTC();

    @Setter @Autowired private ReportGenerator reportGenerator;

    @ModelAttribute("reportTypes")
    public ReportType[] populateReportTypes() {
        return ReportType.values();
    }


    @ModelAttribute("preferredReportType")
    public ReportType populatePreferredReportType() {
        LocalDateTime now = LocalDateTime.now(clock);
        if (now.getHour() < 11) {
            return ReportType.TODAY;
        } else if (now.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
            return ReportType.SUNDAY_FOR_MONDAY;
        }

        return ReportType.TOMORROW;
    }

    @ModelAttribute("teams")
    public List populateUserTeams() {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        Map authDetails = (Map) oAuth2Authentication.getUserAuthentication().getDetails();
        return (List) authDetails.get("teams");
    }



    @RequestMapping(method = RequestMethod.GET)
    public String home(@ModelAttribute("teams") List teams,
                       @ModelAttribute("preferredReportType") ReportType preferredReportType,
                       Map<String, Object> model) {
        Criteria criteria = new Criteria();
        if (teams != null && !teams.isEmpty()) {
            criteria.setTeam((String) teams.get(0));
        }
        criteria.setReportType(preferredReportType);

        model.put("criteria", criteria);
        return "reports/home";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@ModelAttribute Criteria criteria, ModelMap modelMap) {
        modelMap.addAttribute("criteria", criteria);

        Report report = reportGenerator.generate(criteria.getReportType(), criteria.getTeam());
        modelMap.addAttribute("report", report);
        modelMap.addAttribute("reportDate", buildReportDate(criteria.getReportType()));
        logger.debug(criteria.getReportType().format() + " report for " + criteria.getTeam() + " desk generated");
        return "reports/home";
    }

    public String buildReportDate(ReportType preferredReportType) {
        LocalDate today = LocalDate.now(clock);

        if (preferredReportType == ReportType.SUNDAY_FOR_MONDAY) {
            LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            return sunday.format(dateFormat);
        }
        if (preferredReportType == ReportType.TOMORROW) {
            return today.plusDays(1).format(dateFormat);
        }

        return today.format(dateFormat);
    }

}
