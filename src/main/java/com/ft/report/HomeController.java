package com.ft.report;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/reports/home")
    public String home() {
        return "reports/home";
    }
}
