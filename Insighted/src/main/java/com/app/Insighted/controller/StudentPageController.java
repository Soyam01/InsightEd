package com.app.Insighted.controller;

import com.app.Insighted.model.User;
import com.app.Insighted.repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentPageController {

    @Autowired
    private UserRepo userRepo;

    @GetMapping("/feedback")
    public String feedbackPage(Model model){
        model.addAttribute("activepage", "Feedback");
        return "student-feedback";
    }


    @GetMapping("/grades")
    public String gradesPage(Model model){
        return "student-grades";
    }

    @GetMapping("/notification")
    public String notificationPage(Model model){
        model.addAttribute("activepage", "Notification");
        return "student-notifications";
    }
}