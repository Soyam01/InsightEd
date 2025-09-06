package com.app.Insighted.controller;

import com.app.Insighted.model.Assignment;
import com.app.Insighted.model.AssignmentStatus;
import com.app.Insighted.model.TeacherAssignment;
import com.app.Insighted.model.User;
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

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherPageController {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private TeacherAssignmentRepo teacherAssignmentRepo;

    @GetMapping("/manage-assignment")
    public String manageAssignmentPage(Model model, HttpSession session){

        User teacher = userRepository.findByEmail((String) session.getAttribute("email"));

        List<TeacherAssignment> allAssigned = teacherAssignmentRepo.findTeacherAssignmentByTeacher(teacher);

        int totalAssignment = allAssigned.size();

        // Calculate this week's date range
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        System.out.println("Week range: " + startOfWeek + " to " + endOfWeek);

        // Count assignments due this week
        Long dueThisWeekCount = teacherAssignmentRepo.countAssignmentsDueThisWeek(teacher, startOfWeek, endOfWeek);



        model.addAttribute("totalAssignments", totalAssignment);
        model.addAttribute("dueThisWeek", dueThisWeekCount);
        model.addAttribute("assignments", allAssigned);
        model.addAttribute("activepage", "Manage Assignment");
        return "teacher-manage-assignments";
    }


    @GetMapping("/submissions")
    public String submissionPage(Model model, Authentication auth){
        User currentUser = getCurrentUser(auth);


        List<Assignment> allAssignments = assignmentService.findAssignmentsByTeacher(currentUser.getUser_id());
        List<Assignment> pendingAssignments = assignmentService.findPendingAssignmentsForTeacher(currentUser.getUser_id());

        // Calculate stats
        long totalFeedback = allAssignments.stream()
                .filter(a -> a.getStatus() == AssignmentStatus.REVIEWED)
                .count();

        long pendingReviews = pendingAssignments.size();


        model.addAttribute("user", currentUser);
        model.addAttribute("assignments", allAssignments);
        model.addAttribute("totalFeedback", totalFeedback);
        model.addAttribute("pendingReviews", pendingReviews);

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

    private User getCurrentUser(Authentication auth) {
        String email = auth.getName();
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

