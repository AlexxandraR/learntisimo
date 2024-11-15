package com.asos.reservationSystem.services;

import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.domain.entities.User;

import java.util.List;
import java.util.Optional;

public interface MeetingService {

    List<Meeting> getTeacherMeetings(Optional<User> teacher);

    List<Meeting> getStudentMeetings(Optional<User> student);

    void removeMeeting(Long meetingId, Optional<User> teacher);

    Meeting createMeeting(Meeting meeting, Optional<User> teacher);

    Optional<Meeting> removeStudentMeeting(Long meetingId, Optional<User> student);
    List<Meeting> getCourseMeeting(Long courseId, Optional<User> user);

    Meeting addStudentToMeeting(Long meetingId, Optional<User> student);
}
