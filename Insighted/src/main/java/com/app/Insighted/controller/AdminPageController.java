package com.app.Insighted.controller;

import com.app.Insighted.model.User;
import com.app.Insighted.repository.AssignmentRepo;
import com.app.Insighted.repository.UserRepo;
import com.app.Insighted.services.AdminServices;
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
    public String userMangementPage(Model model){
        model.addAttribute("activepage", "User Management");
        return "admin-user-management";
    }

    @GetMapping("/analytics")
    public String analyticsPage(Model model){
        model.addAttribute("activepage", "Analytics");
        return "admin-analytics";
    }

    @PostMapping("/create-user")
    @ResponseBody
    public ResponseEntity<?> createUser(Model model, @RequestBody User user){
        String isUserCreated = adminServices.createUser(user);

        if(isUserCreated.equals("User Created Successfully!")){
            return ResponseEntity.ok().body(Map.of("message", isUserCreated));
        }else {
            return ResponseEntity.badRequest().body(Map.of("message", isUserCreated));
        }
    }
}
