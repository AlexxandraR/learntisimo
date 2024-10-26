package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.UserDto;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.mappers.Mapper;
import com.asos.reservationSystem.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class UserController {
    private final UserService userService;

    private final Mapper<User, UserDto> userMapper;

    public UserController(UserService userService, Mapper<User, UserDto> userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping(path = "/user")
    public ResponseEntity<UserDto> getUser(Principal connectedCustomer){
        Optional<User> user = userService.getUser(connectedCustomer);
        return user.map(userEntity -> {
            UserDto userDto = userMapper.mapToDto(userEntity);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
