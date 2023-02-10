package com.seitov.news.controller;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.seitov.news.exception.UserRegistrationException;
import com.seitov.news.service.AuthService;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    
    @MockBean
    AuthService authService;

    @InjectMocks
    AuthController authController;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getLogin() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/login"))
                    .andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Login"));
    }

    @Test
    public void getRegister() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/register"))
                    .andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Register"));
    }

    @Test
    public void postRegisterSuccess() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(multipart(HttpMethod.POST, "/register")
                        .param("username", "validUsername")
                        .param("password", "somepass123")
                        .param("matchingPassword", "somepass123").with(csrf()))
                    .andExpect(status().isCreated())
                    .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Login"));
    }
    
    @Test
    public void postRegisterTooShortUsername() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(multipart(HttpMethod.POST, "/register")
                        .param("username", "asd")
                        .param("password", "somepass123")
                        .param("matchingPassword", "somepass123").with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Username length must be between 4 and 20 characters"));
    }

    @Test
    public void postRegisterTooLongUsername() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(multipart(HttpMethod.POST, "/register")
                        .param("username", "extremlyLongUsername123")
                        .param("password", "somepass123")
                        .param("matchingPassword", "somepass123").with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Username length must be between 4 and 20 characters"));
    }

    @Test
    public void postRegisterTooShortPassword() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(multipart(HttpMethod.POST, "/register")
                        .param("username", "validUsername")
                        .param("password", "somep")
                        .param("matchingPassword", "somep").with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Password length must be between 8 and 20 characters"));
    }

    @Test
    public void postRegisterTooLongPassword() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(multipart(HttpMethod.POST, "/register")
                        .param("username", "validUsername")
                        .param("password", "extremlyLongPassword123")
                        .param("matchingPassword", "extremlyLongPassword123").with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Password length must be between 8 and 20 characters"));
    }

    @Test
    public void postRegisterAlreadyExistingUsername() throws Exception {
        when(authService.register(any())).thenThrow(new UserRegistrationException("User with this username already exists!"));
        MvcResult mvcResult = this.mockMvc.perform(multipart(HttpMethod.POST, "/register")
                        .param("username", "validUsername")
                        .param("password", "somepass123")
                        .param("matchingPassword", "somepass123").with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("User with this username already exists!"));
    }

    
}
