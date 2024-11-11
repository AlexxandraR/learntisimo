package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.exception.CustomException;
import com.asos.reservationSystem.repositories.MeetingRepository;
import com.asos.reservationSystem.services.MeetingService;
import com.asos.reservationSystem.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Service
public class MeetingServiceImpl implements MeetingService {
    private final MeetingRepository meetingRepository;
    private final UserService userService;


    public MeetingServiceImpl(MeetingRepository meetingRepository, UserService userService) {
        this.meetingRepository = meetingRepository;
        this.userService = userService;
    }

    @Override
    public List<Meeting> getAllTeacherMeetings(Long teacherId) {
        var meetings = meetingRepository.findAllByTeacher_Id(teacherId);
        return meetings.orElse(Collections.emptyList());
    }

    @Override
    public List<Meeting> getAllStudentMeetings(Long studentId) {
        var courses = meetingRepository.findAllByStudent_Id(studentId);
        return courses.orElse(Collections.emptyList());
    }

    @Override
    public void removeMeeting(Long meetingId, Principal connectedUser) {
        var meeting = meetingRepository.findById(meetingId);
        if (meeting.isEmpty()) {
            throw new CustomException("Meeting does not exist.",
                    "Removal of meeting: Meeting with id: " + meetingId + " does not exist.", HttpStatus.NOT_FOUND);
        }

        userService.checkRole(connectedUser, Role.TEACHER);
        userService.checkConnectedToProvidedUser(connectedUser, meeting.get().getTeacher().getId().toString());

        try {
            meetingRepository.deleteById(meetingId);
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while removing meeting.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    public Meeting createMeeting(Meeting meeting) {
        if (meeting == null) {
            throw new CustomException("Meeting is null.",
                    "Creation of meeting: Meeting is null.", HttpStatus.BAD_REQUEST);
        }
        if(meeting.getTeacher() == null){
            throw new CustomException("Teacher is null.",
                    "Creation of meeting: Teacher is null.", HttpStatus.BAD_REQUEST);
        }
        try {
            return meetingRepository.save(meeting);
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while creating meeting.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    public void removeStudentMeeting(Long meetingId, Principal connectedUser) {
        var meeting = meetingRepository.findById(meetingId);
        if (meeting.isEmpty()) {
            throw new CustomException("Meeting does not exist.",
                    "Removal of student from meeting: Meeting with id: " + meetingId + " does not exist.", HttpStatus.NOT_FOUND);
        }

        userService.checkRole(connectedUser, Role.STUDENT);
        userService.checkConnectedToProvidedUser(connectedUser, meeting.get().getStudent().getId().toString());
        try {
            meeting.get().setStudent(null);
            meetingRepository.save(meeting.get());
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while removing student from meeting.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Override
    public List<Meeting> listMeetingsCourse(Long courseId) {
        var meetings = meetingRepository.findAllByCourse_IdAndStudentIsNull(courseId);
        return meetings.orElse(Collections.emptyList());
    }

    @Override
    public void removeStudentFromCourseMeetings(Long courseId, Long studentId) {
        try {
            var meetings = meetingRepository.findAllByCourse_IdAndStudent_Id(courseId, studentId);
            if (!meetings.isEmpty()) {
                for (Meeting meeting : meetings.get()) {
                    meeting.setStudent(null);
                    meetingRepository.save(meeting);
                }
            }
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while removing student from course meetings.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void addStudentToMeeting(Long meetingId, User student) {
        var meeting = meetingRepository.findById(meetingId);
        if (meeting.isEmpty()) {
            throw new CustomException("Meeting does not exist.",
                    "Adding student to meeting: Meeting with id: " + meetingId + " does not exist.", HttpStatus.NOT_FOUND);
        }
        try {
            var updatedMeeting = meeting.get();
            updatedMeeting.setStudent(student);
            meetingRepository.save(updatedMeeting);
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while adding student to meeting.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
