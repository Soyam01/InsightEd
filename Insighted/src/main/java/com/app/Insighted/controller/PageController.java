package com.app.Insighted.controller;

import com.app.Insighted.model.User;
import com.app.Insighted.repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final UserRepo userRepo;

    public PageController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/")
    public String indexPage(){
        return "index";
    }


}
