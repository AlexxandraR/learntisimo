package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.TeachingRequestDto;
import com.asos.reservationSystem.domain.dto.UserDto;
import com.asos.reservationSystem.domain.entities.TeachingRequest;
import com.asos.reservationSystem.domain.entities.TeachingRequestStatus;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.mappers.Mapper;
import com.asos.reservationSystem.services.TeachingRequestService;
import com.asos.reservationSystem.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class TeachingRequestController {
    private final TeachingRequestService requestService;
    private final UserService userService;

    private final Mapper<TeachingRequest, TeachingRequestDto> requestMapper;
    private final Mapper<User, UserDto> userMapper;


    private Logger logger;

    public TeachingRequestController(TeachingRequestService requestService, Mapper<TeachingRequest, TeachingRequestDto> requestMapper, UserService userService, Mapper<User, UserDto> userMapper) {
        this.requestService = requestService;
        this.requestMapper = requestMapper;
        this.logger = LoggerFactory.getLogger(TeachingRequestController.class);
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping(path = "/applyTeacher")
    public void applyTeacher(Principal connectedUser) {
        Optional<User> user = userService.getUser(connectedUser);
        requestService.applyTeacher(user.get());
    }

    @GetMapping(path = "/getTeachingRequests")
    public ResponseEntity<List<TeachingRequestDto>> getTeachingRequests(Principal connectedUser) {
        Optional<User> user = userService.getUser(connectedUser);
        var teachingRequests = requestService.getTeachingRequests(user.get());
        return ResponseEntity.ok(teachingRequests.stream().map(requestMapper::mapToDto).toList());
    }


    @PatchMapping(path = "/acceptRequest/{requestId}")
    public void acceptRequest(@PathVariable Long requestId, @RequestBody UserDto userDto, Principal connectedUser) {
        //TODO: Delete student from Meeting and courses
        if (!userService.getUser(connectedUser).get().getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Only admins can deny requests");
        }
        var user = userMapper.mapFromDto(userDto);
        userService.acceptTeacher(user);
        requestService.updateTeachingRequestStatus(requestId, TeachingRequestStatus.APPROVED);


    }

    @PatchMapping(path = "/denyRequest/{requestId}")
    public void denyRequest(@PathVariable Long requestId, @RequestBody UserDto userDto, Principal connectedUser) {
        if (!userService.getUser(connectedUser).get().getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Only admins can deny requests");
        }
        var user = userMapper.mapFromDto(userDto);
        userService.denyTeacher(user);
        requestService.updateTeachingRequestStatus(requestId, TeachingRequestStatus.REJECTED);
    }


}
