package com.app.Insighted.services;

import com.app.Insighted.repository.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendWelcomeEmail(String emailTo, String firstName){
        try {
            // 1. Create Thymeleaf context
            org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
            context.setVariable("user", firstName);
            context.setVariable("email", emailTo);

            // 2. Process HTML template
            String body = templateEngine.process("welcome-email", context);

            // 3. Create MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(emailTo);
            helper.setSubject("Welcome to InsightEd!");
            helper.setText(body, true); // true = HTML

            // 4. Send email
            mailSender.send(message);

            System.out.println("Welcome email sent to " + emailTo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void accountCreatedEmail(String emailTo, String createdByEmail, String createdBy, String firstName, String lastName, String password, String role){
        try {
            // 1. Create Thymeleaf context
            org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
            context.setVariable("createdByEmail", createdByEmail);
            context.setVariable("createdBy", createdBy);
            context.setVariable("user", firstName + " " + lastName);
            context.setVariable("password", password);
            context.setVariable("email", emailTo);
            context.setVariable("role", role);
            context.setVariable("createdDate", LocalDateTime.now());

            // 2. Process HTML template
            String body = templateEngine.process("user-created-email", context);

            // 3. Create MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(emailTo);
            helper.setSubject("Welcome to InsightEd!");
            helper.setText(body, true); // true = HTML

            // 4. Send email
            mailSender.send(message);

            System.out.println("Welcome email sent to " + emailTo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetPasswordOtpEmail(String otp, String emailTo){
        try {
            // 1. Create Thymeleaf context
            org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
            context.setVariable("email", emailTo);
            context.setVariable("otpCode", otp);

            // 2. Process HTML template
            String body = templateEngine.process("otp-email", context);

            // 3. Create MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(emailTo);
            helper.setSubject("Reset password Code-InsightEd");
            helper.setText(body, true); // true = HTML

            // 4. Send email
            mailSender.send(message);

            System.out.println("Reset password otp sent to " + emailTo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
