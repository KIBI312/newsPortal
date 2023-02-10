package com.seitov.news.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.seitov.news.dto.RegistrationDto;
import com.seitov.news.entity.User;
import com.seitov.news.exception.UserRegistrationException;
import com.seitov.news.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegistrationDto registrationDto;
    private User user;

    @BeforeEach
    public void initData() {
        //given
        registrationDto = new RegistrationDto();
        registrationDto.setUsername("ValidUsername");
        registrationDto.setPassword("somepass123");
        registrationDto.setMatchingPassword("somepass123");
        user = new User();
        user.setUsername("ValidUsername");
        user.setPassword("$2a$10$rpPgdUYLgmCtZL46nXuZtORdf/ANc99nyxHrJgtO2nzN2WkaqTioa");
        user.setRole("ROLE_USER");
    }

    @Test
    public void registerSuccessfully() {
        //when
        when(userRepository.findByUsername("ValidUsername")).thenReturn(Optional.ofNullable(null));
        when(passwordEncoder.encode("somepass123"))
            .thenReturn("$2a$10$rpPgdUYLgmCtZL46nXuZtORdf/ANc99nyxHrJgtO2nzN2WkaqTioa");
        when(userRepository.save(user)).thenReturn(user);
        //then
        assertEquals(user, authService.register(registrationDto));
    }

    @Test
    public void registerWithExistingUsername() {
        //when
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        Exception ex = assertThrows(UserRegistrationException.class, () -> authService.register(registrationDto));
        //then
        assertEquals("User with this username already exists!", ex.getMessage());
    }

}
