package com.seitov.news.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    
    @GetMapping(path = "/login")
    public String login() {
        return "login";
    }

}
