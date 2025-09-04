package com.app.Insighted.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/teacher")
public class TeacherPageController {


    @GetMapping("/manage-assignment")
    public String manageAssignmentPage(Model model){
        model.addAttribute("activepage", "Manage Assignment");
        return "teacher-manage-assignments";
    }


    @GetMapping("/submissions")
    public String feedbackPage(Model model){
        model.addAttribute("activepage", "Student Submission");
        return "teacher-feedback";
    }

    @GetMapping("/grades")
    public String gradesPages(Model model){
        model.addAttribute("activepage", "Grades");
        return "teacher-grades";
    }

    @GetMapping("/notification")
    public String notificationsPage(Model model){
        model.addAttribute("activepage", "Notification");
        return "teacher-notification";
    }
}

