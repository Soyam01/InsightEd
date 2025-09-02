package com.app.Insighted.controller;

import com.app.Insighted.model.User;
import com.app.Insighted.services.AdminServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @Autowired
    private AdminServices adminServices;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, HttpSession session){
        model.addAttribute("activepage", "Dashboard");
        return "admin-dashboard";
    }

    @GetMapping("/user-management")
    public String userMangementPage(Model model){
        model.addAttribute("activepage", "User Management");
        return "admin-user-management";
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
