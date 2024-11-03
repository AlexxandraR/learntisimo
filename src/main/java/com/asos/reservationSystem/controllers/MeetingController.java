package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.CourseDto;
import com.asos.reservationSystem.domain.dto.MeetingDto;
import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.mappers.Mapper;
import com.asos.reservationSystem.services.MeetingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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


}
