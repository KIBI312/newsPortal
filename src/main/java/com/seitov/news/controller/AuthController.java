package com.seitov.news.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.seitov.news.dto.RegistrationDto;
import com.seitov.news.exception.UserRegistrationException;
import com.seitov.news.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @GetMapping(path = "/login")
    public String login() {
        return "login";
    }


    @GetMapping("/register")
    public String register(HttpServletRequest request, Model model) {
        RegistrationDto registrationDto = new RegistrationDto();
        model.addAttribute("registrationDto", registrationDto);
        return "register";
    }

    @PostMapping("/register")
    public String registerAccount(@ModelAttribute("registrationDto") @Valid RegistrationDto registrationDto, BindingResult result, Model model,  HttpServletRequest request){
        if(result.hasErrors()){
            return "register";
        }
        try {
            authService.register(registrationDto);
        } catch (UserRegistrationException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
        return "login";
    }

}
