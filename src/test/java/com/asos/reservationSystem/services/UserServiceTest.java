package com.asos.reservationSystem.services;

import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.repositories.UserRepository;
import com.asos.reservationSystem.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testGetUserByEmail_UserExists() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.getUserByEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    public void testGetUserByEmail_UserDoesNotExist() {
        // Arrange
        when(userRepository.findById(0L)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserByEmail("nonexistent@example.com");

        // Assert
        assertTrue(result.isEmpty());
    }
}