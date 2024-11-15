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
import org.springframework.web.multipart.MultipartFile;

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
        Optional<User> user = userService.getUser(connectedCustomer);
        return user.map(userEntity -> {
            UserDto userDto = userMapper.mapToDto(userEntity);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/photo")
    public ResponseEntity<String> uploadPhoto(Principal connectedCustomer, @RequestParam("photo") MultipartFile photo) {
        Optional<User> user = userService.getUser(connectedCustomer);
        userService.saveUserPhoto(user, photo);
        return ResponseEntity.ok("Image uploaded successfully.");
    }

    @GetMapping("/photo/{id}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        byte[] photoBytes = userService.getUserPhoto(id);
        return ResponseEntity.ok(photoBytes);
    }

    @PutMapping(path = "/updateUserProfile")
    public ResponseEntity<UserDto> updateUserProfile(@RequestBody UserDto userDto, Principal connectedUser) {
        try {
            Optional<User> user = userService.getUser(connectedUser);
            User userData = userMapper.mapFromDto(userDto);
            userService.updateProfile(user, userData);
            logger.info("Successfully updated user profile with id: " + user.get().getId() + " at: "
                    + LocalDateTime.now());
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } catch (Exception e) {
            throw new CustomException("Wrong format of data.", "Update profile: Wrong format of data.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/updateEmail")
    public ResponseEntity<UserDto> updateEmail(@RequestBody EmailChangeDto newEmail, Principal connectedUser) {
        Optional<User> user = userService.getUser(connectedUser);
        userService.updateEmail(user, newEmail.getEmail(), newEmail.getNewEmail(), newEmail.getPassword());
        logger.info("Successfully updated email for user: " + connectedUser.getName() + " at: " + LocalDateTime.now());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(path = "/updatePassword")
    public ResponseEntity<UserDto> updatePassword(@RequestBody NewPasswordDTO newPassword, Principal connectedUser) {
        Optional<User> user = userService.getUser(connectedUser);
        userService.updatePassword(user, newPassword.getNewPassword(), newPassword.getPassword());
        logger.info("Successfully updated password for user: " + connectedUser.getName() + " at: " + LocalDateTime.now());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
