package com.seitov.news.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target( ElementType.TYPE_USE )
@Documented
@Constraint(validatedBy = MatchingPasswordsCheck.class)
public @interface MatchingPasswords {
    
    String message() default "Passwords not matching";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default {};

}
