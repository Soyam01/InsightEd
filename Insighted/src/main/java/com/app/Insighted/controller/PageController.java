package com.app.Insighted.controller;

import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String indexPage(){
        return "index";
    }

    @GetMapping("/dashboard")
    public String studentDashboard(Model model){
        return "student-dashboard";
    }
}
