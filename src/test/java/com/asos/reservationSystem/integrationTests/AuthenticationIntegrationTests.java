package com.asos.reservationSystem.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthenticationIntegrationTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthenticationIntegrationTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testThatAuthenticateUserReturnsUnauthorizedWhenUserEmailIsWrong() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "student1@student.com",
                "password", "student"
        ));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isUnauthorized()
        ).andReturn();
    }

    @Test
    public void testThatAuthenticateUserReturnsUnauthorizedWhenUserPasswordIsWrong() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "student@student.com",
                "password", "student1"
        ));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isUnauthorized()
        ).andReturn();
    }

    @Test
    public void testThatAuthenticateUserIsSuccessful() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "student@student.com",
                "password", "student"
        ));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();
    }
}
