package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.CourseDto;
import com.asos.reservationSystem.domain.dto.MeetingDto;
import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.mappers.Mapper;
import com.asos.reservationSystem.services.MeetingService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class MeetingController {
    private final MeetingService meetingService;

    private final Mapper<Meeting, MeetingDto> meetingMapper;

    public MeetingController(MeetingService meetingService, Mapper<Meeting, MeetingDto> meetingMapper) {
        this.meetingService = meetingService;
        this.meetingMapper = meetingMapper;
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
    public void saveMeeting(@RequestBody MeetingDto meetingDto){
        meetingService.createMeeting(meetingMapper.mapFromDto(meetingDto));
    }


}
