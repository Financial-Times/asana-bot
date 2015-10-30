package com.ft.report;

import com.ft.report.model.*;
import java.util.function.Function;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "report")
@Profile("web")
@Controller
@RequestMapping("/")
public class ReportsController {

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

    @Setter private Clock clock = Clock.systemUTC();

    @Setter @Autowired private ReportGenerator reportGenerator;

    @Setter @Getter private Map<String, Desk> desks;

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

    @SuppressWarnings("unchecked")
    @ModelAttribute("teams")
    public Map populateUserTeams() {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        Map authDetails = (Map) oAuth2Authentication.getUserAuthentication().getDetails();
        List<String> userDesks = Optional.ofNullable((List<String>) authDetails.get("teams"))
                .orElse(Collections.emptyList());
        Map<String, List<Project>> deskProjects =  userDesks.stream()
                .collect(Collectors.toMap(Function.identity(), this::findDeskProjects));
        return deskProjects;
    }

    public List<Project> findDeskProjects(String desk) {
        return desks.get(desk).getProjects();
    }


    @RequestMapping(method = RequestMethod.GET)
    public String home(@ModelAttribute("teams") Map teams,
                       @ModelAttribute("preferredReportType") ReportType preferredReportType,
                       Map<String, Object> model) {
        Criteria criteria = new Criteria();
        if (teams != null && !teams.isEmpty()) {
            criteria.setTeam((String) teams.keySet().toArray()[0]);
            criteria.assignProject(desks.get(criteria.getTeam()).getProjects());
        }
        criteria.setReportType(preferredReportType);

        model.put("criteria", criteria);
        return "reports/home";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@ModelAttribute Criteria criteria, ModelMap modelMap) {
        modelMap.addAttribute("criteria", criteria);

        criteria.lookupProject(desks.get(criteria.getTeam()).getProjects());
        Report report = reportGenerator.generate(criteria);
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
