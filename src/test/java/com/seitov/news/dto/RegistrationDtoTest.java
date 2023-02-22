package com.seitov.news.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
public class RegistrationDtoTest {

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    @Test
    public void isValidWithCorrectData() {
        //given
        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setUsername("ValidUsername");
        registrationDto.setPassword("somepass123");
        registrationDto.setMatchingPassword("somepass123");
        //then
        assertEquals(validator.validate(registrationDto).size(),0);
    }

    @Test
    public void isValidWithNonMatchingPasswords() {
        //given
        RegistrationDto registrationDto = new RegistrationDto();
        registrationDto.setUsername("ValidUsername");
        registrationDto.setPassword("somepass123");
        registrationDto.setMatchingPassword("nonmatching");
        //then
        assertEquals(validator.validate(registrationDto).size(),1);
    }

}
