package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.repositories.MeetingRepository;
import com.asos.reservationSystem.services.MeetingService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MeetingServiceImpl implements MeetingService {
    private MeetingRepository meetingRepository;

    public MeetingServiceImpl(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    @Override
    public List<Meeting> getAllTeacherMeetings(Long teacherId) {
        var meetings = meetingRepository.findAllByTeacher_Id(teacherId);
        if (meetings.isEmpty()) {
            System.out.println("No meetings found for teacher with id " + teacherId);
            return Collections.emptyList();
        } else {
            return meetings.get();
        }
    }

    @Override
    public List<Meeting> getAllStudentMeetings(Long studentId) {
        var courses = meetingRepository.findAllByStudent_Id(studentId);
        if (courses.isEmpty()) {
            System.out.println("No courses found for teacher with id " + studentId);
            return Collections.emptyList();
        } else {
            return courses.get();
        }
    }
}
