package com.ft.services;

import com.ft.config.Config;
import com.ft.report.model.Report;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Created by ola.okejimi on 16/11/15.
 */
@ConfigurationProperties(prefix = "sendgrid")
@Service
public class EmailService {

    private Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    Config config;

    @Getter
    @Setter
    private String apikey;

    public boolean sendEmail(final Report report, final String team) {

        SendGrid sendGrid = new SendGrid(apikey);
        SendGrid.Email email = createEmail(report, team);

        try {
            return sendGrid.send(email).getStatus();
        } catch (SendGridException e) {
            throw new RuntimeException(e);
        }
    }

    private SendGrid.Email createEmail(final Report report, final String team) {
        SendGrid.Email email = new SendGrid.Email();
        email.setFrom("noreply.asanareport.com");
        email.addTo(getEmailAddress(team));
        email.setSubject(MessageFormat.format("VIDEOS - {0,date,full}", new Date()));

        final Context ctx = new Context();
        ctx.setVariable("report", report);

        final String content = templateEngine.process("email/email", ctx);
        email.setHtml(content);

        return email;
    }

    public boolean isEmailTeam(final String team) {
        return config.getEmailTeams().stream()
                .map(s -> s.get("name"))
                .collect(Collectors.toList())
                .contains(team);
    }

    public String getEmailAddress(final String team) {
        return config.getEmailTeams().stream()
                .filter(s -> s.get("name").equalsIgnoreCase(team))
                .map(s -> s.get("email"))
                .findFirst().get();
    }


}
