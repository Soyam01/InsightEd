package com.app.Insighted.controller;

import com.app.Insighted.model.Assignment;
import com.app.Insighted.model.User;
import com.app.Insighted.repository.AssignmentRepo;
import com.app.Insighted.repository.UserRepo;
import com.app.Insighted.services.AdminServices;
import com.app.Insighted.services.MailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @Autowired
    private AdminServices adminServices;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AssignmentRepo assignmentRepo;

    @Autowired
    private MailService mailService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, HttpSession session){

        int allUsersCount = userRepo.findAll().size();

        int allAssignmentCount = assignmentRepo.findAll().size();

        model.addAttribute("totalAssignments", allAssignmentCount);
        model.addAttribute("totalUsers", allUsersCount);
        model.addAttribute("activepage", "Dashboard");
        return "admin-dashboard";
    }

    @GetMapping("/user-management")
    public String userManagementPage(Model model){
        List<User> allUsers = userRepo.findAll();
        int studentCount = 0;
        int teacherCount = 0;
        int adminCount = 0;

        for (User user: allUsers){
            if("ROLE_ADMIN".equals(user.getRole())){
                adminCount++;
            } else if ("ROLE_TEACHER".equals(user.getRole())) {
                teacherCount++;
            } else if ("ROLE_STUDENT".equals(user.getRole())) {
                studentCount++;
            }
        }

        model.addAttribute("users", allUsers);
        model.addAttribute("admins", adminCount);
        model.addAttribute("teachers", teacherCount);
        model.addAttribute("students", studentCount);
        model.addAttribute("roleStudent", "ROLE_STUDENT");
        model.addAttribute("roleTeacher", "ROLE_TEACHER");
        model.addAttribute("roleAdmin", "ROLE_ADMIN");
        model.addAttribute("activepage", "User Management");
        return "admin-user-management";
    }

    @GetMapping("/analytics")
    public String analyticsPage(Model model){

        List<Assignment> assignments = assignmentRepo.findAll();

        int totalAssignments = assignments.size();
        int graded = 0;
        int pendingReview = 0;

        for(Assignment assignment: assignments){
            if (assignment.getGrade() != null){
                graded++;
            }else {
                pendingReview++;
            }
        }

        int allUsers = userRepo.findAll().size();

        model.addAttribute("activeUsers", allUsers);
        model.addAttribute("totalSubmission", totalAssignments);
        model.addAttribute("graded", graded);
        model.addAttribute("pendingReview", pendingReview);
        model.addAttribute("activepage", "Analytics");
        return "admin-analytics";
    }

    @PostMapping("/create-user")
    @ResponseBody
    public ResponseEntity<?> createUser(Model model, @RequestBody User user, HttpSession session){
        String isUserCreated = adminServices.createUser(user);

        User admin = userRepo.findByEmail((String) session.getAttribute("email"));
        String adminName = admin.getFirstName() + " " + admin.getLastName();
        if(isUserCreated.equals("User Created Successfully!")){
            mailService.accountCreatedEmail(user.getEmail(), admin.getEmail(),adminName,user.getFirstName(),user.getLastName(),user.getPassword(), user.getRole());
            return ResponseEntity.ok().body(Map.of("message", isUserCreated));
        }else {
            return ResponseEntity.badRequest().body(Map.of("message", isUserCreated));
        }
    }

    @GetMapping("/remove-user/{id}")
    public String removeUser(@PathVariable Long id){
        userRepo.deleteByUserId(id);
        return "redirect:/admin/user-management";
    }
}
