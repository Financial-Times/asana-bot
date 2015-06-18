package com.ft.login;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Profile("web")
@Controller
public class LogoutController {

    @RequestMapping("/logout")
    public String logout() {
        return "login/logout";
    }
}
