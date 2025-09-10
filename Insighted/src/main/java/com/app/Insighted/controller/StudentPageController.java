package com.app.Insighted.controller;

import com.app.Insighted.model.Assignment;
import com.app.Insighted.model.AssignmentStatus;
import com.app.Insighted.model.TeacherAssignment;
import com.app.Insighted.model.User;
import com.app.Insighted.repository.AssignmentRepo;
import com.app.Insighted.repository.TeacherAssignmentRepo;
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
import java.util.Objects;

@Controller
@RequestMapping("/student")
public class StudentPageController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AssignmentRepo assignmentRepo;

    @Autowired
    private TeacherAssignmentRepo teacherAssignmentRepo;

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
    public String gradesPage(Model model, HttpSession session){
        User student = userRepo.findByEmail((String) session.getAttribute("email"));

        List<Assignment> allAssignments = assignmentRepo.findByStudentOrderBySubmittedAtDesc(student);
        // Filter only graded assignments (non-null grade)
        List<Integer> grades = allAssignments.stream()
                .map(Assignment::getGrade)
                .filter(Objects::nonNull)
                .toList();

        int highestGrade = 0;
        int lowestGrade = 0;
        double avgGrade = 0.0;

        if (!grades.isEmpty()) {
            highestGrade = grades.stream().max(Integer::compare).orElse(0);
            lowestGrade = grades.stream().min(Integer::compare).orElse(0);
            avgGrade = grades.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        }

        // Add attributes to frontend
        model.addAttribute("assignments", allAssignments);
        model.addAttribute("highestGrade", highestGrade);
        model.addAttribute("lowestGrade", lowestGrade);
        model.addAttribute("avgGrade", avgGrade);

        return "student-grades";
    }

    @GetMapping("/notification")
    public String notificationPage(Model model, HttpSession session) {
        model.addAttribute("activepage", "Notification");

        // Get logged-in student
        String email = (String) session.getAttribute("email");
        User student = userRepo.findByEmail(email);

        // Fetch notifications:
        // 1. New teacher assignments (all, or you can filter by course/student’s enrolled classes if you track enrollment)
        List<TeacherAssignment> newAssignments = teacherAssignmentRepo.findAll();

        // 2. Student’s assignments that are graded
        List<Assignment> gradedAssignments = assignmentRepo.findByStudentOrderBySubmittedAtDesc(student)
                .stream()
                .filter(a -> a.getGrade() != null)
                .toList();

        model.addAttribute("newAssignments", newAssignments);
        model.addAttribute("gradedAssignments", gradedAssignments);

        return "student-notifications";
    }


    private User getCurrentUser(Authentication auth) {
        String email = auth.getName();
        return userRepo.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}