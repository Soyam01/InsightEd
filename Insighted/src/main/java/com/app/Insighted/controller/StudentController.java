package com.app.Insighted.controller;

import com.app.Insighted.model.Assignment;
import com.app.Insighted.model.AssignmentStatus;
import com.app.Insighted.model.User;
import com.app.Insighted.repository.AssignmentRepo;
import com.app.Insighted.repository.UserRepo;
import com.app.Insighted.services.AssignmentService;
import com.app.Insighted.services.DocumentProcessingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private DocumentProcessingService documentService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private AssignmentRepo assignmentRepo;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth, HttpSession session) {
        User currentUser = getCurrentUser(auth);


        List<Assignment> assignments = assignmentService.findAssignmentsByStudent(currentUser.getUser_id());

        // Calculate stats
        long totalAssignments = assignments.size();
        long pendingReview = assignments.stream()
                .filter(a -> a.getStatus() == AssignmentStatus.SUBMITTED || a.getStatus() == AssignmentStatus.IN_REVIEW)
                .count();

        Double avgGrade = assignments.stream()
                .filter(a -> a.getGrade() != null)
                .mapToInt(Assignment::getGrade)
                .average()
                .orElse(0.0);

        model.addAttribute("user", currentUser);
        model.addAttribute("assignments", assignments);
        model.addAttribute("totalAssignments", totalAssignments);
        model.addAttribute("pendingReview", pendingReview);
        model.addAttribute("avgGrade", Math.round(avgGrade));
        User user = userRepository.findByEmail((String)session.getAttribute("email"));

        model.addAttribute("username", user.getFirstName() + " " + user.getLastName());
        model.addAttribute("activepage", "Dashboard");

        return "student-dashboard";
    }

    @GetMapping("/upload-assignment")
    public String uploadAssignmentPage(Model model, Authentication auth) {
        User currentUser = getCurrentUser(auth);



        // Get available teachers for assignment
        List<User> users = userRepository.findAll();

        List<User> teachers = new ArrayList<>();

        for (User user: users){
            if("ROLE_TEACHER".equals(user.getRole())){
                teachers.add(user);
            }
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("teachers", teachers);
        model.addAttribute("assignment", new Assignment());
        model.addAttribute("activepage", "Upload Assignment");

        return "student-upload-assignment";
    }

    @PostMapping("/upload-assignment")
    public String uploadAssignment(@RequestParam("assignmentFile") MultipartFile file,
                                   @RequestParam("teacherId") Long teacherId,
                                   @ModelAttribute Assignment assignment,
                                   Authentication auth,
                                   RedirectAttributes redirectAttributes) {
        try {
            User student = getCurrentUser(auth);
            User teacher = userRepository.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));


            assignment.setStudent(student);
            assignment.setTeacher(teacher);
            assignment.setStatus(AssignmentStatus.SUBMITTED);

            // Process and save the document
            documentService.processUploadedDocument(file, assignment);

            redirectAttributes.addFlashAttribute("success", "Assignment uploaded successfully!");
            return "redirect:/student/dashboard";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Upload failed: " + e.getMessage());
            return "redirect:/student/upload-assignment";
        }
    }

    @GetMapping("/assignment/{assignmentId}")
    public String viewAssignment(@PathVariable Long assignmentId, Model model, Authentication auth) {
        User student = getCurrentUser(auth);

        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));


        model.addAttribute("assignment", assignment);
        model.addAttribute("sections", assignment.getSections());
        model.addAttribute("isStudent", true);

        return "document-viewer";
    }

    private User getCurrentUser(Authentication auth) {
        String email = auth.getName();
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}