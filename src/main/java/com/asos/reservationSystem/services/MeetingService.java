package com.asos.reservationSystem.services;

import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.Meeting;

import java.util.List;

public interface MeetingService {

    List<Meeting> getAllTeacherMeetings(Long teacherId);

    List<Meeting> getAllStudentMeetings(Long studentId);
}
