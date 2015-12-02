package com.ft.report;

import com.ft.report.date.ReportDateBuilder;
import com.ft.report.model.Criteria;
import com.ft.report.model.Desk;
import com.ft.report.model.Report;
import com.ft.report.model.ReportType;
import com.ft.services.EmailService;
import com.sendgrid.SendGridException;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "report")
@Profile("web")
@Controller
@RequestMapping("/")
public class ReportsController {

    private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

    @Setter
    private Clock clock = Clock.systemUTC();

    @Setter
    @Autowired
    private ReportGenerator reportGenerator;

    @Setter
    @Getter
    private Map<String, Desk> desks;

    @Autowired @Setter
    private EmailService emailService;

    @Autowired @Setter
    private ReportDateBuilder reportDateBuilder;

    @ModelAttribute("reportTypes")
    public ReportType[] populateReportTypes() {
        return ReportType.values();
    }

    @ModelAttribute("reportTypesMap")
    public Map<String, List<Map<String, String>>> populateReportTypesMap() {
        return ReportType.getReportTypesByCategories();
    }


    @ModelAttribute("preferredReportType")
    public ReportType populatePreferredReportType() {
        //TODO could extend that to weekends
        LocalDateTime now = LocalDateTime.now(clock);
        if (now.getHour() < 11) {
            return ReportType.TODAY;
        } else if (now.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
            return ReportType.SUNDAY_FOR_MONDAY;
        }

        return ReportType.TOMORROW;
    }

    @SuppressWarnings("unchecked")
    @ModelAttribute("userTeams")
    public Map populateUserDesks() {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        Map authDetails = (Map) oAuth2Authentication.getUserAuthentication().getDetails();
        List<String> userDesks = Optional.ofNullable((List<String>) authDetails.get("teams"))
                .orElse(Collections.emptyList());
        Map<String, Desk> deskProjects = userDesks.stream()
                .collect(Collectors.toMap(Function.identity(), this::findDesk));
        return deskProjects;
    }

    @ModelAttribute("showEmailLink")
    public boolean showEmailLink(final String team) {
        return emailService.isEmailTeam(team);
    }

    public Desk findDesk(String desk) {
        return desks.get(desk);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String home(@ModelAttribute("userTeams") Map teams,
                       @ModelAttribute("preferredReportType") ReportType preferredReportType,
                       Map<String, Object> model) {
        Criteria criteria = new Criteria();
        if (teams != null && !teams.isEmpty()) {
            criteria.setTeam((String) teams.keySet().toArray()[0]);
        }
        criteria.setReportType(preferredReportType);
        criteria.setProjects(new ArrayList<>());

        model.put("criteria", criteria);
        return "reports/home";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createMultiProject(@ModelAttribute Criteria criteria,
                                     @RequestParam(value = "sendEmail", defaultValue = "false") final Boolean sendEmail,
                                     ModelMap modelMap) {

        final List<Report> reports = reportGenerator.generate(criteria);
        final Map<String, Report> reportsMap = reports.stream()
                .collect(Collectors.toMap(entry -> entry.getProject().getName(), Function.identity()));

        String reportDate = reportDateBuilder.buildReportDate(criteria.getReportType());
        modelMap.addAttribute("reportDate", reportDate);
        modelMap.addAttribute("criteria", criteria);

        modelMap.addAttribute("reports", reportsMap);
        if (sendEmail) {
            final String title = "Weekend Plan " + reportDate;
            final String team = criteria.getTeam();
            modelMap.addAttribute("emailSent", sendEmail(team, title, reports.toArray(new Report[reports.size()])));
        }

        return "reports/home";
    }

    private String sendEmail(final String team, final String title, final Report... report) {
        String message = "Problem sending email";
        try {
            if (emailService.sendEmail(team, title, report))
                message = "Your message has been sent";
        } catch (SendGridException e) {
            logger.error("problem sending email {}", e);
        }
        return message;
    }

}
