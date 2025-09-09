package com.app.Insighted.services;

import com.app.Insighted.model.User;
import com.app.Insighted.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class ResetPasswordService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MailService mailService;


    // Store OTP with expiration (in-memory for now)
    private final Map<String, OtpData> otpStore = new HashMap<>();

    public boolean generateAndSendOtp(String email) {
        User user = userRepo.findUserByEmail(email).orElse(null);
        if (user == null) return false;

        // Generate 6-digit OTP
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        // Store with 5 min expiry
        otpStore.put(email, new OtpData(otp, LocalDateTime.now().plusMinutes(5)));

        // Send email
        mailService.resetPasswordOtpEmail(otp, email);

        return true;
    }

    public boolean verifyOtp(String email, String otp) {
        OtpData data = otpStore.get(email);
        if (data == null) return false;
        if (data.expiry.isBefore(LocalDateTime.now())) {
            otpStore.remove(email);
            return false; // expired
        }
        return data.otp.equals(otp);
    }

    public boolean resetPassword(String email, String newPassword) {

        User user = userRepo.findUserByEmail(email).orElse(null);
        if (user == null) return false;

        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();

        user.setPassword(bCrypt.encode(newPassword));
        userRepo.save(user);

        otpStore.remove(email); // invalidate OTP
        return true;
    }

    // Helper record
    private static class OtpData {
        String otp;
        LocalDateTime expiry;
        OtpData(String otp, LocalDateTime expiry) {
            this.otp = otp;
            this.expiry = expiry;
        }
    }
}
