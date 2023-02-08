package com.seitov.news.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
public class RegistrationDtoTests {
    
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    @Test
    public void isValidWithCorrectData() {
        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setUsername("ValidUsername");
        registrationDto.setPassword("somepass123");
        registrationDto.setMatchingPassword("somepass123");
        assertEquals(validator.validate(registrationDto).size(),0);
    }

    @Test
    public void isValidWithNonMathcingPasswords() {

        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setUsername("ValidUsername");
        registrationDto.setPassword("somepass123");
        registrationDto.setMatchingPassword("nonmatching");
        assertEquals(validator.validate(registrationDto).size(),1);
    }



}
