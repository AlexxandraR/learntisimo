package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.UserDto;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.mappers.Mapper;
import com.asos.reservationSystem.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    public ResponseEntity<UserDto> getUser(Principal connectedCustomer){
        Optional<User> user = userService.getUser(connectedCustomer);
        return user.map(userEntity -> {
            UserDto userDto = userMapper.mapToDto(userEntity);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(path = "/userEmail")
    public ResponseEntity<UserDto> getUserEmail(@RequestBody String email){
        Optional<User> user = userService.getUserByEmail(email.trim());
        return user.map(userEntity -> {
            UserDto userDto = userMapper.mapToDto(userEntity);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(path = "/updateUserProfile")
    public ResponseEntity<UserDto> updateUserProfile(@RequestBody UserDto userDto, Principal connectedUser){
        User user = userMapper.mapFromDto(userDto);
        userService.updateProfile(user, connectedUser);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PatchMapping(path = "/updateEmail")
    public ResponseEntity<UserDto> updateEmail(@RequestBody String newEmail, Principal connectedUser){
        userService.udpateEmail(connectedUser, newEmail.trim());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(path = "/updatePassword")
    public ResponseEntity<UserDto> updatePassword(@RequestBody String newPassword, Principal connectedUser){
        userService.updatePassword(connectedUser, newPassword.trim());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
