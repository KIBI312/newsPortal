package com.seitov.news.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.seitov.news.dto.RegistrationDto;
import com.seitov.news.entity.User;
import com.seitov.news.exception.UserRegistrationException;
import com.seitov.news.repository.UserRepository;

@Service
public class AuthService {
    
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegistrationDto registrationDto) throws UserRegistrationException {
        if(userRepository.findByUsername(registrationDto.getUsername()).isPresent()){
            throw new UserRegistrationException("User with this username already exists!");
        }
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole("ROLE_USER");
        return userRepository.save(user);
    }

}
