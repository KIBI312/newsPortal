package com.seitov.news.validation;

import com.seitov.news.dto.RegistrationDto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MatchingPasswordsCheck implements ConstraintValidator<MatchingPasswords, RegistrationDto> {

    @Override
    public void initialize(MatchingPasswords constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(RegistrationDto value, ConstraintValidatorContext context) {
        boolean isValid = value.getPassword().equals(value.getMatchingPassword());
        if(!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("matchingPassword").addConstraintViolation();
        }
        return isValid;
    }
    
}
