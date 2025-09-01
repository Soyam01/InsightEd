package com.app.Insighted.services;

import com.app.Insighted.dto.LoginDto;
import com.app.Insighted.model.User;
import com.app.Insighted.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServices {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    public String CreateUser(User user){
        Optional<User> existingUser = userRepo.findUserByEmail(user.getEmail());

        if (existingUser.isPresent()){
            return "User already exists!";
        }

        User userToCreate = new User();
        //setting user detais
        userToCreate.setFirstName(user.getFirstName());
        userToCreate.setLastName(user.getLastName());
        userToCreate.setEmail(user.getEmail());
        userToCreate.setRole("ROLE_STUDENT");

        //hash pwd and set
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
        userToCreate.setPassword(bCrypt.encode(user.getPassword()));

        try{
            userRepo.save(userToCreate);
            return "Account Successfully Created!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed To Create User! Try Again Later";
        }
    }

    public String verifyUser(LoginDto loginDto, HttpServletRequest request) {
        try {
            // Only authenticate once
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );

            if(authentication.isAuthenticated()) {
                // Set SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Start session and store email
                HttpSession session = request.getSession(true);
                session.setAttribute("email", loginDto.getEmail());
                session.setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        SecurityContextHolder.getContext()
                );

                return authentication.getAuthorities().iterator().next().getAuthority();
            }
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return null;
        }

        return null;
    }

}
