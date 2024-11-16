package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.TeachingRequestDto;
import com.asos.reservationSystem.domain.dto.UserDto;
import com.asos.reservationSystem.domain.entities.TeachingRequest;
import com.asos.reservationSystem.domain.entities.TeachingRequestStatus;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.mappers.Mapper;
import com.asos.reservationSystem.services.TeachingRequestService;
import com.asos.reservationSystem.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class TeachingRequestController {
    private final TeachingRequestService requestService;
    private final UserService userService;

    private final Mapper<TeachingRequest, TeachingRequestDto> requestMapper;
    private final Mapper<User, UserDto> userMapper;


    private final Logger logger;

    public TeachingRequestController(TeachingRequestService requestService, Mapper<TeachingRequest,
            TeachingRequestDto> requestMapper, UserService userService, Mapper<User, UserDto> userMapper) {
        this.requestService = requestService;
        this.requestMapper = requestMapper;
        this.logger = LoggerFactory.getLogger(TeachingRequestController.class);
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PatchMapping(path = "/applyTeacher")
    public ResponseEntity<Void> applyTeacher(Principal connectedUser) {
        Optional<User> user = userService.getUser(connectedUser);
        requestService.applyTeacher(user);
        logger.info("Teaching request: Successfully applied teacher with id: " + user.get().getId() + " at: "
                + LocalDateTime.now());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/getTeachingRequests")
    public List<TeachingRequestDto> getTeachingRequests(Principal connectedUser) {
        Optional<User> user = userService.getUser(connectedUser);
        var teachingRequests = requestService.getTeachingRequests(user);
        logger.info("Teaching request: Successfully retrieved teaching requests for user with id: " + user.get().getId()
                + " at: " + LocalDateTime.now());
        return teachingRequests.stream().map(requestMapper::mapToDto).toList();
    }


    @PatchMapping(path = "/acceptRequest/{requestId}")
    public ResponseEntity<Void> acceptRequest(@PathVariable Long requestId, @RequestBody UserDto userDto,
                                              Principal connectedUser) {
        Optional<User> admin = userService.getUser(connectedUser);
        User user = userMapper.mapFromDto(userDto);
        requestService.updateTeachingRequestStatus(requestId, user, admin, TeachingRequestStatus.APPROVED);
        logger.info("Teaching request: Successfully accepted teaching request with id: " + requestId
                + " for user with id: " + user.getId() + " at: " + LocalDateTime.now());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(path = "/denyRequest/{requestId}")
    public ResponseEntity<Void> denyRequest(@PathVariable Long requestId, @RequestBody UserDto userDto,
                                            Principal connectedUser) {
        Optional<User> admin = userService.getUser(connectedUser);
        User user = userMapper.mapFromDto(userDto);
        requestService.updateTeachingRequestStatus(requestId, user, admin, TeachingRequestStatus.REJECTED);
        logger.info("Teaching request: Successfully denied teaching request with id: " + requestId
                + " for user with id: " + user.getId() + " at: " + LocalDateTime.now());
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
