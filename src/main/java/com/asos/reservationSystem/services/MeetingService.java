package com.asos.reservationSystem.services;

import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.domain.entities.User;

import java.security.Principal;
import java.util.List;

public interface MeetingService {

    List<Meeting> getAllTeacherMeetings(Long teacherId);

    List<Meeting> getAllStudentMeetings(Long studentId);

    void removeMeeting(Long meetingId, Principal connectedUser);

    Meeting createMeeting(Meeting meeting);

    void removeStudentMeeting(Long meetingId, Principal connectedUser);
    List<Meeting> listMeetingsCourse(Long courseId);

    void removeStudentFromCourseMeetings(Long courseId, Long studentId);

    void addStudentToMeeting(Long meetingId, User student);
}
