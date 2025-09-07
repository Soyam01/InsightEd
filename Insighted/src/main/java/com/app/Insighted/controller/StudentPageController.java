package com.app.Insighted.controller;

import com.app.Insighted.model.Assignment;
import com.app.Insighted.model.AssignmentStatus;
import com.app.Insighted.model.User;
import com.app.Insighted.repository.UserRepo;
import com.app.Insighted.services.AssignmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentPageController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AssignmentService assignmentService;

    @GetMapping("/feedback")
    public String feedbackPage(Model model, Authentication auth) {
        // Get logged-in student
        User student = getCurrentUser(auth);

        // Fetch assignments of the student
        List<Assignment> assignments = assignmentService.findAssignmentsByStudent(student.getUser_id());

        // Only reviewed or in-review assignments are relevant for feedback
        List<Assignment> reviewedAssignments = assignments.stream()
                .filter(a -> a.getStatus() == AssignmentStatus.REVIEWED || a.getStatus() == AssignmentStatus.SUBMITTED)
                .toList();

        model.addAttribute("assignments", reviewedAssignments);
        model.addAttribute("user", student);
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

    private User getCurrentUser(Authentication auth) {
        String email = auth.getName();
        return userRepo.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}