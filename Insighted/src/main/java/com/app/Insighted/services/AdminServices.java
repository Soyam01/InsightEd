package com.app.Insighted.services;

import com.app.Insighted.model.User;
import com.app.Insighted.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServices {

    @Autowired
    private UserRepo userRepo;

    public String createUser(User user){
        Optional<User> existingUser = userRepo.findUserByEmail(user.getEmail());

        if (existingUser.isPresent()){
            return "Email Already in Use!!";
        }

        User toSave = new User();
        toSave.setFirstName(user.getFirstName());
        toSave.setLastName(user.getLastName());
        toSave.setEmail(user.getEmail());

        if(user.getRole().equals("admin")) {
            toSave.setRole("ROLE_ADMIN");
        }else {
            toSave.setRole("ROLE_TEACHER");
        }

        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
        toSave.setPassword(bCrypt.encode(user.getPassword()));

        userRepo.save(toSave);
        return "User Created Successfully!";
    }
}
