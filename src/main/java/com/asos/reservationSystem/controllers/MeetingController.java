package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.MeetingDto;
import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.mappers.Mapper;
import com.asos.reservationSystem.services.MeetingService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class MeetingController {
    private final MeetingService meetingService;
    private final UserService userService;

    private final Mapper<Meeting, MeetingDto> meetingMapper;

    private final Logger logger;

    public MeetingController(MeetingService meetingService, Mapper<Meeting, MeetingDto> meetingMapper,
                             UserService userService) {
        this.meetingService = meetingService;
        this.meetingMapper = meetingMapper;
        this.logger = LoggerFactory.getLogger(MeetingController.class);
        this.userService = userService;
    }

    @GetMapping(path = "/teacherMeetings")
    public List<MeetingDto> getTeacherMeetings(Principal connectedUser) {
        Optional<User> teacher = userService.getUser(connectedUser);
        List<Meeting> meetings = meetingService.getTeacherMeetings(teacher);
        logger.info("Get teacher's meetings: Successfully retrieved all meetings for teacher with id: "
                + teacher.get().getId()
                + " at: " + LocalDateTime.now());
        return meetings.stream().map(meetingMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/studentMeetings")
    public List<MeetingDto> getStudentMeetings(Principal connectedUser) {
        Optional<User> student = userService.getUser(connectedUser);
        List<Meeting> meetings = meetingService.getStudentMeetings(student);
        logger.info("Get student's meetings: Successfully retrieved all meetings for student with id: "
                + student.get().getId()
                + " at: " + LocalDateTime.now());
        return meetings.stream().map(meetingMapper::mapToDto).collect(Collectors.toList());
    }

    @DeleteMapping(path = "/removeMeeting/{meetingId}")
    public ResponseEntity<Void> removeMeeting(@PathVariable Long meetingId, Principal connectedUser) {
        Optional<User> teacher = userService.getUser(connectedUser);
        meetingService.removeMeeting(meetingId, teacher);
        logger.info("Removal of meeting: Successfully removed meeting with id: " + meetingId + " at: "
                + LocalDateTime.now());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(path = "/removeStudentMeeting/{meetingId}")
    public ResponseEntity<MeetingDto> removeStudentMeeting(@PathVariable Long meetingId, Principal connectedUser) {
        Optional<User> user = userService.getUser(connectedUser);
        Optional<Meeting> meeting = meetingService.removeStudentMeeting(meetingId, user);
        logger.info("Removal of student from meeting: Successfully removed student with id: " + user.get().getId()
                + " from meeting with id: " + meetingId + " at: " + LocalDateTime.now());
        return meeting.map(meetingEntity -> {
            MeetingDto meetingDto = meetingMapper.mapToDto(meetingEntity);
            return new ResponseEntity<>(meetingDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(path = "/createMeeting")
    public ResponseEntity<MeetingDto> saveMeeting(@RequestBody MeetingDto meetingDto, Principal connectedUser) {
        Optional<User> teacher = userService.getUser(connectedUser);
        Meeting newMeeting = meetingService.createMeeting(meetingMapper.mapFromDto(meetingDto), teacher);
        MeetingDto newMeetingDto = meetingMapper.mapToDto(newMeeting);
        logger.info("Creation of meeting: Successfully created meeting with id: " + newMeeting.getId() + " at: "
                + LocalDateTime.now());
        return new ResponseEntity<>(newMeetingDto, HttpStatus.CREATED);
    }

    @GetMapping(path = "/courseMeetings/{courseId}")
    public List<MeetingDto> getCourseMeetings(@PathVariable Long courseId, Principal connectedUser) {
        Optional<User> student = userService.getUser(connectedUser);
        List<Meeting> meetings = meetingService.getCourseMeeting(courseId, student);
        logger.info("Get course meeting: Successfully retrieved all meetings for course with id: " + courseId
                + " at: " + LocalDateTime.now());
        return meetings.stream().map(meetingMapper::mapToDto).collect(Collectors.toList());
    }

    @PatchMapping(path = "/addStudentToMeeting/{meetingId}")
    public ResponseEntity<MeetingDto> addStudentToMeeting(Principal connectedUser, @PathVariable Long meetingId) {
        Optional<User> student = userService.getUser(connectedUser);
        Meeting meeting = meetingService.addStudentToMeeting(meetingId, student);
        logger.info("Adding student to meeting: Successfully added student with id: " + student.get().getId()
                + " to meeting with id: "
                + meetingId + " at: " + LocalDateTime.now());
        return new ResponseEntity<>(meetingMapper.mapToDto(meeting), HttpStatus.OK);
    }


}
