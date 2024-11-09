package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.MeetingDto;
import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.domain.entities.User;
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
    public List<MeetingDto> getAllTeacherCourses(@RequestBody String teacherId){
        return meetingService.getAllTeacherMeetings(Long.parseLong(teacherId.trim())).stream().map(meetingMapper::mapToDto).
                collect(Collectors.toList());
    }

    @PostMapping (path = "/meetingsStudent")
    public List<MeetingDto> getAllStudentCourses(@RequestBody String studentId) {
        return meetingService.getAllStudentMeetings(Long.parseLong(studentId.trim())).stream().map(meetingMapper::mapToDto).
                collect(Collectors.toList());
    }

    @DeleteMapping(path = "/removeMeeting/{meetingId}")
    public ResponseEntity<Void> removeMeeting(@PathVariable String meetingId) {
        try {
            meetingService.removeMeeting(Long.parseLong(meetingId.trim()));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping (path = "/removeStudentMeeting")
    public ResponseEntity<Void> removeStudentMeeting(@RequestBody String meetingId) {
        try {
            meetingService.removeStudentMeeting(Long.parseLong(meetingId.trim()));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/createMeeting")
    public MeetingDto saveMeeting(@RequestBody MeetingDto meetingDto){
        var newMeeting = meetingService.createMeeting(meetingMapper.mapFromDto(meetingDto));
        return meetingMapper.mapToDto(newMeeting);
    }

    @GetMapping(path = "/listMeetingsCourse/{courseId}")
    public List<MeetingDto> listMeetingsCourse(@PathVariable String courseId){
        return meetingService.listMeetingsCourse(Long.parseLong(courseId.trim())).stream().map(meetingMapper::mapToDto).
                collect(Collectors.toList());
    }

    @GetMapping (path = "/meetingStudentAdd/{meetingId}")
    public void addStudentToMeeting(Principal connectedUser, @PathVariable String meetingId) {
        Optional<User> user = userService.getUser(connectedUser);
        meetingService.addStudentToMeeting(Long.parseLong(meetingId.trim()), user.get());
    }


}
