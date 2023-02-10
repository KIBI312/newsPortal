package com.seitov.news.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.seitov.news.entity.User;
import com.seitov.news.repository.UserRepository;
import com.seitov.news.security.AppUserDetailsService;

@ExtendWith(MockitoExtension.class)
public class AppUserDetailsServiceTest {
    
    @Mock
    UserRepository userRepository;

    @InjectMocks
    AppUserDetailsService userDetailsService;

    @Test
    public void loadUserByUsernameSuccessfully() {
        //given
        User user = new User();
        UserDetails userDetails = user;
        user.setUsername("testUser");
        //when
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        //then
        assertEquals(userDetails, userDetailsService.loadUserByUsername("testUser"));
    }

    @Test
    public void loadUserByUsernameNonExisting() {
        //given
        String username = "anyUsername";
        //when
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(null));
        //then
        Exception ex = assertThrows(UsernameNotFoundException.class,
                        () -> userDetailsService.loadUserByUsername(username));
        assertEquals("No user found with this username: " + username, ex.getMessage());
    }


}
