package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.domain.dto.MeetingDto;
import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.domain.entities.User;
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

    @Override
    public void removeMeeting(Long meetingId) {
        meetingRepository.deleteById(meetingId);
    }

    @Override
    public Meeting createMeeting(Meeting meeting) {
        return meetingRepository.save(meeting);
    }

    @Override
    public void removeStudentMeeting(Long meetingId) {
        var meeting = meetingRepository.findById(meetingId);
        if (meeting.isEmpty()) {
            System.out.println("No meeting found with id " + meetingId);
        } else {
            meeting.get().setStudent(null);
            meetingRepository.save(meeting.get());
        }
    }



    @Override
    public List<Meeting> listMeetingsCourse(Long courseId) {
        var meetings = meetingRepository.findAllByCourse_IdAndStudentIsNull(courseId);
        if (meetings.isEmpty()) {
            return Collections.emptyList();
        } else {
            return meetings.get();
        }
    }

    @Override
    public void removeStudentFromCourseMeetings(Long courseId, Long studentId) {
        var meetings = meetingRepository.findAllByCourse_IdAndStudent_Id(courseId, studentId);
        if (meetings.isEmpty()) {
            System.out.println("No meetings found for course with id " + courseId + " and student with id " + studentId);
        } else {
            for (var meeting : meetings.get()) {
                meeting.setStudent(null);
                meetingRepository.save(meeting);
            }
        }
    }

    @Override
    public void addStudentToMeeting(Long meetingId, User student) {
        var meeting = meetingRepository.findById(meetingId);
        if (meeting.isEmpty()) {
            System.out.println("No meeting found with id " + meetingId);
        } else {
            var updatedMeeting = meeting.get();
            updatedMeeting.setStudent(student);
            meetingRepository.save(updatedMeeting);
        }
    }
}
