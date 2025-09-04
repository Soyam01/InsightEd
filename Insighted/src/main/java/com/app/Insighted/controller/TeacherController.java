package com.app.Insighted.controller;


import com.app.Insighted.dto.FeedbackRequest;
import com.app.Insighted.dto.GradeRequest;
import com.app.Insighted.model.*;
import com.app.Insighted.repository.AssignmentRepo;
import com.app.Insighted.repository.DocumentSectionRepo;
import com.app.Insighted.repository.FeedbackRepo;
import com.app.Insighted.repository.UserRepo;
import com.app.Insighted.services.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

        @Autowired
        private AssignmentService assignmentService;

        @Autowired
        private FeedbackRepo feedbackService;

        @Autowired
        private UserRepo userRepository;

        @Autowired
        private AssignmentRepo assignmentRepo;

        @Autowired
        private DocumentSectionRepo documentSectionRepository;

        @GetMapping("/dashboard")
        public String dashboard(Model model, Authentication auth) {
            User currentUser = getCurrentUser(auth);


            List<Assignment> allAssignments = assignmentService.findAssignmentsByTeacher(currentUser.getUser_id());
            List<Assignment> pendingAssignments = assignmentService.findPendingAssignmentsForTeacher(currentUser.getUser_id());

            // Calculate stats
            long totalFeedback = allAssignments.stream()
                    .filter(a -> a.getStatus() == AssignmentStatus.REVIEWED)
                    .count();

            long pendingReviews = pendingAssignments.size();

            Double avgGrade = allAssignments.stream()
                    .filter(a -> a.getGrade() != null)
                    .mapToInt(Assignment::getGrade)
                    .average()
                    .orElse(0.0);

            model.addAttribute("user", currentUser);
            model.addAttribute("assignments", allAssignments);
            model.addAttribute("totalFeedback", totalFeedback);
            model.addAttribute("pendingReviews", pendingReviews);
            model.addAttribute("avgGrade", Math.round(avgGrade * 10.0) / 10.0);
            model.addAttribute("activepage", "Dashboard");

            return "teacher-feedback";
        }

        @GetMapping("/review/{assignmentId}")
        public String reviewAssignment(@PathVariable Long assignmentId, Model model, Authentication auth) {
            User teacher = getCurrentUser(auth);


            Assignment assignment = assignmentRepo.findById(assignmentId)
                    .orElseThrow(() -> new RuntimeException("Assignment not found"));


            model.addAttribute("assignment", assignment);
            model.addAttribute("sections", assignment.getSections());
            model.addAttribute("isTeacher", true);
            model.addAttribute("currentUser", teacher);

            return "document-viewer";
        }

        @PostMapping("/feedback")
        @ResponseBody
        public ResponseEntity<?> addFeedback(@RequestBody FeedbackRequest request, Authentication auth) {
            try {
                User teacher = getCurrentUser(auth);


                DocumentSection section = documentSectionRepository.findById(request.getSectionId())
                        .orElseThrow(() -> new RuntimeException("Section not found"));


                Feedback feedback = new Feedback();
                feedback.setComment(request.getComment());
                feedback.setHighlightedText(request.getHighlightedText());
                feedback.setDocumentSection(section);
                feedback.setTeacher(teacher);

                feedbackService.save(feedback);

                return ResponseEntity.ok().build();

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error saving feedback: " + e.getMessage());
            }
        }

        @PostMapping("/grade/{assignmentId}")
        @ResponseBody
        public ResponseEntity<?> saveGrade(@PathVariable Long assignmentId,
                                           @RequestBody GradeRequest request,
                                           Authentication auth) {
            try {
                User teacher = getCurrentUser(auth);

                Assignment assignment = assignmentRepo.findById(assignmentId)
                        .orElseThrow(() -> new RuntimeException("Assignment not found"));

                assignment.setGrade(request.getGrade());
                assignment.setStatus(AssignmentStatus.REVIEWED);
                assignment.setReviewedAt(LocalDateTime.now());

                assignmentRepo.save(assignment);

                return ResponseEntity.ok().build();

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error saving grade: " + e.getMessage());
            }
        }

        private User getCurrentUser(Authentication auth) {
            String email = auth.getName();
            return userRepository.findUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
    }
