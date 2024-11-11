package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.EmailChangeDto;
import com.asos.reservationSystem.domain.dto.NewPasswordDTO;
import com.asos.reservationSystem.domain.dto.UserDto;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.exception.CustomException;
import com.asos.reservationSystem.mappers.Mapper;
import com.asos.reservationSystem.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class UserController {
    private final UserService userService;

    private final Mapper<User, UserDto> userMapper;

    private final Logger logger;

    public UserController(UserService userService, Mapper<User, UserDto> userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.logger = LoggerFactory.getLogger(UserController.class);
    }

    @GetMapping(path = "/user")
    public ResponseEntity<UserDto> getUser(Principal connectedCustomer) {
        try {
            Optional<User> user = userService.getUser(connectedCustomer);
            return user.map(userEntity -> {
                UserDto userDto = userMapper.mapToDto(userEntity);
                return new ResponseEntity<>(userDto, HttpStatus.OK);
            }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (CustomException e) {
            logger.error("Error retrieving user: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error retrieving user: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/userEmail")
    public ResponseEntity<UserDto> getUserEmail(@RequestBody String email) {
        try {
            Optional<User> user = userService.getUserByEmail(email.trim());
            return user.map(userEntity -> {
                UserDto userDto = userMapper.mapToDto(userEntity);
                return new ResponseEntity<>(userDto, HttpStatus.OK);
            }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (CustomException e) {
            logger.error("Error retrieving user by email: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error retrieving user by email: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/updateUserProfile")
    public ResponseEntity<UserDto> updateUserProfile(@RequestBody UserDto userDto, Principal connectedUser) {
        try {
            User user = userMapper.mapFromDto(userDto);
            userService.updateProfile(user, connectedUser);
            logger.info("Successfully updated user profile with id: " + user.getId() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error updating user profile: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error updating user profile: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping(path = "/updateEmail")
    public ResponseEntity<UserDto> updateEmail(@RequestBody EmailChangeDto newEmail, Principal connectedUser) {
        try {
            userService.updateEmail(connectedUser, newEmail.getNewEmail(), newEmail.getPassword());
            logger.info("Successfully updated email for user: " + connectedUser.getName() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error updating email: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error updating email: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping(path = "/updatePassword")
    public ResponseEntity<UserDto> updatePassword(@RequestBody NewPasswordDTO newPassword, Principal connectedUser) {
        try {
            userService.updatePassword(connectedUser, newPassword.getNewPassword(), newPassword.getPassword());
            logger.info("Successfully updated password for user: " + connectedUser.getName() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error updating password: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error updating password: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
