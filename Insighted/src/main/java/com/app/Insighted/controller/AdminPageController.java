package com.app.Insighted.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, HttpSession session){
        return "admin-dashboard";
    }

    @GetMapping("/user-management")
    public String userMangementPage(){
        return "admin-user-management";
    }
}
