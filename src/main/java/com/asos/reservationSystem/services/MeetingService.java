package com.asos.reservationSystem.services;

import com.asos.reservationSystem.domain.dto.MeetingDto;
import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.domain.entities.User;

import java.util.List;

public interface MeetingService {

    List<Meeting> getAllTeacherMeetings(Long teacherId);

    List<Meeting> getAllStudentMeetings(Long studentId);

    void removeMeeting(Long meetingId);

    Meeting createMeeting(Meeting meeting);

    void removeStudentMeeting(Long meetingId);
    List<Meeting> listMeetingsCourse(Long courseId);

    void removeStudentFromCourseMeetings(Long courseId, Long studentId);

    void addStudentToMeeting(Long meetingId, User student);
}
