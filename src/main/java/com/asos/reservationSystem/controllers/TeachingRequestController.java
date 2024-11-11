package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.TeachingRequestDto;
import com.asos.reservationSystem.domain.dto.UserDto;
import com.asos.reservationSystem.domain.entities.TeachingRequest;
import com.asos.reservationSystem.domain.entities.TeachingRequestStatus;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.exception.CustomException;
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

    public TeachingRequestController(TeachingRequestService requestService, Mapper<TeachingRequest, TeachingRequestDto> requestMapper, UserService userService, Mapper<User, UserDto> userMapper) {
        this.requestService = requestService;
        this.requestMapper = requestMapper;
        this.logger = LoggerFactory.getLogger(TeachingRequestController.class);
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping(path = "/applyTeacher")
    public ResponseEntity<Void> applyTeacher(Principal connectedUser) {
        try {
            userService.checkRole(connectedUser, Role.STUDENT);
            Optional<User> user = userService.getUser(connectedUser);
            requestService.applyTeacher(user.get());
            logger.info("Successfully applied teacher with id: " + user.get().getId() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error applying teacher: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error applying teacher: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/getTeachingRequests")
    public ResponseEntity<List<TeachingRequestDto>> getTeachingRequests(Principal connectedUser) {
        try {
            userService.checkRole(connectedUser, Role.ADMIN);
            Optional<User> user = userService.getUser(connectedUser);
            var teachingRequests = requestService.getTeachingRequests(user.get());
            logger.info("Successfully retrieved teaching requests for user with id: " + user.get().getId() + " at: " + LocalDateTime.now());
            return ResponseEntity.ok(teachingRequests.stream().map(requestMapper::mapToDto).toList());
        } catch (CustomException e) {
            logger.error("Error retrieving teaching requests: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error retrieving teaching requests: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PatchMapping(path = "/acceptRequest/{requestId}")
    public ResponseEntity<Void> acceptRequest(@PathVariable Long requestId, @RequestBody UserDto userDto, Principal connectedUser) {
        try {
            userService.checkRole(connectedUser, Role.ADMIN);
            var user = userMapper.mapFromDto(userDto);
            userService.acceptTeacher(user);
            requestService.updateTeachingRequestStatus(requestId, TeachingRequestStatus.APPROVED);
            logger.info("Successfully accepted teaching request with id: " + requestId + " for user with id: " + user.getId() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error accepting teaching request: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error accepting teaching request: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping(path = "/denyRequest/{requestId}")
    public ResponseEntity<Void> denyRequest(@PathVariable Long requestId, @RequestBody UserDto userDto, Principal connectedUser) {
        try {
            userService.checkRole(connectedUser, Role.ADMIN);
            var user = userMapper.mapFromDto(userDto);
            userService.denyTeacher(user);
            requestService.updateTeachingRequestStatus(requestId, TeachingRequestStatus.REJECTED);
            logger.info("Successfully denied teaching request with id: " + requestId + " for user with id: " + user.getId() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error denying teaching request: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error denying teaching request: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
