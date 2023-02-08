package com.seitov.news.dto;

import org.hibernate.validator.constraints.Length;

import com.seitov.news.validation.MatchingPasswords;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@MatchingPasswords(message = "Passwords are not matching")
public class RegistrationDto {
    
    @NotNull
    @Length(min = 4, max = 20, message = "Username length must be between 4 and 20 characters")
    private String username;
    @NotNull
    @Length(min = 8, max = 20, message = "Password length must be between 8 and 20 characters")
    private String password;
    @NotNull
    private String matchingPassword;

}
