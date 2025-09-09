package com.app.Insighted.controller;

import com.app.Insighted.services.ResetPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/auth")
public class ResetPasswordController {

    @Autowired
    private ResetPasswordService resetService;

    // Step 1: Show Forgot Password form
    @GetMapping("/reset-password")
    public String showResetPasswordForm() {
        return "reset-password";
    }

    // Step 2: Handle email submission & send OTP
    @PostMapping("/reset-email-otp")
    public String sendOtp(@RequestParam String email, Model model) {
        boolean sent = resetService.generateAndSendOtp(email);
        if (!sent) {
            model.addAttribute("error", "No account found with that email.");
            return "reset-password";
        }
        System.out.println(email + "form erset email otp");
        model.addAttribute("email", email);
        return "verify-otp"; // page to enter OTP
    }

    // Step 3: Verify OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam("otpDigits") List<String> otpDigits,
                            Model model) {
        String otp = String.join("", otpDigits);

        boolean valid = resetService.verifyOtp(email, otp);
        if (!valid) {
            model.addAttribute("error", "Invalid or expired OTP.");
            model.addAttribute("email", email);
            return "verify-otp"; // ⬅️ here is the problem
        }
        // OTP is correct → go to password reset page
        model.addAttribute("email", email);
        return "new-password";
    }

    @GetMapping("/new-password")
    public String showNewPasswordPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "new-password";
    }

    // Step 4: Reset password
    @PostMapping("/new-password")
    public String resetPassword(@RequestParam String email,
                                @RequestParam String newPassword,
                                Model model) {
        boolean success = resetService.resetPassword(email, newPassword);
        if (!success) {
            model.addAttribute("error", "Invalid request or expired OTP.");
            model.addAttribute("email", email);
            return "new-password";
        }
        model.addAttribute("message", "Password reset successful! You can now log in.");
        return "login"; // redirect to login page
    }
}
