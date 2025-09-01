package com.app.Insighted.controller;

import com.app.Insighted.dto.LoginDto;
import com.app.Insighted.model.User;
import com.app.Insighted.services.AuthServices;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private AuthServices authServices;

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<?> signup(@RequestBody User user) {
        String isCreated = authServices.CreateUser(user);

        if (isCreated.equals("Account Successfully Created!")) {
            return ResponseEntity.ok(Map.of("message", isCreated, "type", "success"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", isCreated, "type", "error"));
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginData, HttpServletRequest request){
        String role = authServices.verifyUser(loginData, request);

        if(role == null){
            return ResponseEntity.badRequest().body(Map.of("message","Invalid Credentials"));
        }

        // Role-based redirect URL
        String redirectUrl;
        switch (role) {
            case "ROLE_ADMIN" -> redirectUrl = "/admin/dashboard";
            case "ROLE_TEACHER" -> redirectUrl = "/teacher/dashboard";
            default -> redirectUrl = "/dashboard";
        }

        return ResponseEntity.ok(Map.of(
                "message", "Login Successful",
                "redirect", redirectUrl
        ));
    }
}
