package com.ft.services;

import com.ft.config.Config;
import com.ft.report.model.Report;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.stream.Collectors;


@ConfigurationProperties(prefix = "sendgrid")
@Service
public class EmailService {

    @Autowired @Setter
    private TemplateEngine templateEngine;

    @Autowired @Setter
    Config config;

    @Getter
    @Setter
    private String apikey;

    public boolean sendEmail(final String team, final String title, final Report ... report) throws SendGridException {

        SendGrid sendGrid = new SendGrid(apikey);
        SendGrid.Email email = createEmail(team, title, report);
        return sendGrid.send(email).getStatus();
    }

    private SendGrid.Email createEmail(final String team, final String title, final Report ... report) {
        SendGrid.Email email = new SendGrid.Email();
        email.setFrom("noreply.asanareport.com");
        email.addTo(getEmailAddress(team));
        email.setSubject(title);

        final Context ctx = new Context();
        ctx.setVariable("reports", report);

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
        return getEmailAttribute(team, "email");
    }

    private String getEmailAttribute(final String team, final String attribute){
        return config.getEmailTeams().stream()
                .filter(s -> s.get("name").equalsIgnoreCase(team))
                .map(s -> s.get(attribute))
                .findFirst().get();
    }


}
