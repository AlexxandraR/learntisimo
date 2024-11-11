package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.MeetingDto;
import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.exception.CustomException;
import com.asos.reservationSystem.mappers.Mapper;
import com.asos.reservationSystem.services.MeetingService;
import com.asos.reservationSystem.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class MeetingController {
    private final MeetingService meetingService;
    private final UserService userService;

    private final Mapper<Meeting, MeetingDto> meetingMapper;

    private final Logger logger;

    public MeetingController(MeetingService meetingService, Mapper<Meeting, MeetingDto> meetingMapper, UserService userService) {
        this.meetingService = meetingService;
        this.meetingMapper = meetingMapper;
        this.logger = LoggerFactory.getLogger(MeetingController.class);
        this.userService = userService;
    }

    @PostMapping(path = "/meetingsTeacher")
    public ResponseEntity<List<MeetingDto>> getAllTeacherCourses(@RequestBody String teacherId, Principal connectedUser) {
        try {
            userService.checkRole(connectedUser, Role.TEACHER);
            userService.checkConnectedToProvidedUser(connectedUser, teacherId);
            List<Meeting> meetings = meetingService.getAllTeacherMeetings(Long.parseLong(teacherId.trim()));
            List<MeetingDto> meetingDtos = meetings.stream().map(meetingMapper::mapToDto).collect(Collectors.toList());
            logger.info("Successfully retrieved all meetings for teacher with id: " + teacherId + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(meetingDtos, HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error retrieving meetings for teacher: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error retrieving meetings for teacher: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/meetingsStudent")
    public ResponseEntity<List<MeetingDto>> getAllStudentMeetings(@RequestBody String studentId, Principal connectedUser) {
        try {
            userService.checkRole(connectedUser, Role.STUDENT);
            userService.checkConnectedToProvidedUser(connectedUser, studentId);
            List<Meeting> meetings = meetingService.getAllStudentMeetings(Long.parseLong(studentId.trim()));
            List<MeetingDto> meetingDtos = meetings.stream().map(meetingMapper::mapToDto).collect(Collectors.toList());
            logger.info("Successfully retrieved all meetings for student with id: " + studentId + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(meetingDtos, HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error retrieving meetings for student: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error retrieving meetings for student: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/removeMeeting/{meetingId}")
    public ResponseEntity<Void> removeMeeting(@PathVariable String meetingId, Principal connectedUser) {
        try {
            meetingService.removeMeeting(Long.parseLong(meetingId.trim()), connectedUser);
            logger.info("Successfully removed meeting with id: " + meetingId + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error removing meeting: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (NoSuchElementException e) {
            logger.error("Meeting not found: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("Unexpected error removing meeting: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping(path = "/removeStudentMeeting")
    public ResponseEntity<Void> removeStudentMeeting(@RequestBody String meetingId, Principal connectedUser) {
        try {
            meetingService.removeStudentMeeting(Long.parseLong(meetingId.trim()), connectedUser);
            logger.info("Successfully removed student from meeting with id: " + meetingId + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error removing student from meeting: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (NoSuchElementException e) {
            logger.error("Meeting not found: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("Unexpected error removing student from meeting: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/createMeeting")
    public ResponseEntity<MeetingDto> saveMeeting(@RequestBody MeetingDto meetingDto, Principal connectedUser) {
        try {
            userService.checkRole(connectedUser, Role.TEACHER);
            userService.checkConnectedToProvidedUserByEmail(connectedUser, meetingDto.getTeacher().getEmail());
            var newMeeting = meetingService.createMeeting(meetingMapper.mapFromDto(meetingDto));
            MeetingDto newMeetingDto = meetingMapper.mapToDto(newMeeting);
            logger.info("Successfully created meeting with id: " + newMeeting.getId() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(newMeetingDto, HttpStatus.CREATED);
        } catch (CustomException e) {
            logger.error("Error creating meeting: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error creating meeting: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/listMeetingsCourse/{courseId}")
    public ResponseEntity<List<MeetingDto>> listMeetingsCourse(@PathVariable String courseId, Principal connectedUser) {
        try {
            userService.checkRole(connectedUser, Role.STUDENT);
            List<Meeting> meetings = meetingService.listMeetingsCourse(Long.parseLong(courseId.trim()));
            List<MeetingDto> meetingDtos = meetings.stream().map(meetingMapper::mapToDto).collect(Collectors.toList());
            logger.info("Successfully retrieved all meetings for course with id: " + courseId + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(meetingDtos, HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error retrieving meetings for course: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error retrieving meetings for course: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/meetingStudentAdd/{meetingId}")
    public ResponseEntity<Void> addStudentToMeeting(Principal connectedUser, @PathVariable String meetingId) {
        try {
            userService.checkRole(connectedUser, Role.STUDENT);
            Optional<User> user = userService.getUser(connectedUser);
            meetingService.addStudentToMeeting(Long.parseLong(meetingId.trim()), user.get());
            logger.info("Successfully added student with id: " + user.get().getId() + " to meeting with id: " + meetingId + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error adding student to meeting: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error adding student to meeting: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
