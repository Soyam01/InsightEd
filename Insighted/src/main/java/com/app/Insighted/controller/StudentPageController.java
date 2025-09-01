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

    @GetMapping("/dashboard")
    public String studentDashboard(Model model, HttpSession session){
        User user = userRepo.findByEmail((String)session.getAttribute("email"));

        model.addAttribute("username", user.getFirstName() + " " + user.getLastName());
        return "student-dashboard";
    }
}
