package com.app.Insighted.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/teacher")
public class TeacherPageController {

    @GetMapping("/dashboard")
    public String teacherDashboard(Model model){
        model.addAttribute("activepage", "Dashboard");
        return "teacher-dashboard";
    }
}
