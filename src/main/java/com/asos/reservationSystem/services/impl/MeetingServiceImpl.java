package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.exception.CustomException;
import com.asos.reservationSystem.repositories.MeetingRepository;
import com.asos.reservationSystem.services.MeetingService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MeetingServiceImpl implements MeetingService {
    private final MeetingRepository meetingRepository;


    public MeetingServiceImpl(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    @Override
    public List<Meeting> getTeacherMeetings(Optional<User> teacher) {
        if(teacher.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Get teacher's meetings: User does not exist.", HttpStatus.NOT_FOUND);
        }
        if(teacher.get().getRole() != Role.TEACHER){
            throw new CustomException("Only teacher can use this endpoint: /teacherMeetings.",
                    "Get teacher's meetings: Only teacher can use this endpoint: /teacherMeetings.",
                    HttpStatus.BAD_REQUEST);
        }
        return new ArrayList<>(meetingRepository.findByTeacherId(teacher.get().getId()));
    }

    @Override
    public List<Meeting> getStudentMeetings(Optional<User> student) {
        if(student.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Get student's meetings: User does not exist.", HttpStatus.NOT_FOUND);
        }
        if(student.get().getRole() != Role.STUDENT){
            throw new CustomException("Only student can use this endpoint: /studentMeetings.",
                    "Get student's meetings: Only student can use this endpoint: /studentMeetings.",
                    HttpStatus.BAD_REQUEST);
        }
        return new ArrayList<>(meetingRepository.findByStudentId(student.get().getId()));
    }

    @Override
    public void removeMeeting(Long meetingId, Optional<User> teacher) {
        Optional<Meeting> meeting = meetingRepository.findById(meetingId);
        if (meeting.isEmpty()) {
            throw new CustomException("Meeting does not exist.",
                    "Removal of meeting: Meeting with id: " + meetingId + " does not exist.", HttpStatus.NOT_FOUND);
        }
        else if(teacher.isEmpty()){
            throw new CustomException("Teacher does not exist.",
                    "Removal of meeting: Teacher does not exist.", HttpStatus.NOT_FOUND);
        }
        else if (teacher.get().getRole() != Role.TEACHER) {
            throw new CustomException("Only teacher can delete meeting.",
                    "Removal of meeting: Only teacher can delete meeting.", HttpStatus.BAD_REQUEST);
        }
        meetingRepository.deleteById(meetingId);
    }
    @Override
    public Meeting createMeeting(Meeting meeting, Optional<User> teacher) {
        if (meeting == null) {
            throw new CustomException("Meeting is null.",
                    "Creation of meeting: Meeting is null.", HttpStatus.BAD_REQUEST);
        }
        else if(teacher.isEmpty()){
            throw new CustomException("Teacher does not exist.",
                    "Creation of meeting: Teacher does not exist.", HttpStatus.NOT_FOUND);
        }
        else if (teacher.get().getRole() != Role.TEACHER) {
            throw new CustomException("Only teacher can create meeting.",
                    "Creation of meeting: Only teacher can create meeting.", HttpStatus.BAD_REQUEST);
        }
        else if (meeting.getBeginning().isBefore(LocalDateTime.now())) {
            throw new CustomException("Cannot schedule meeting in the past.",
                    "Creation of meeting: Meeting is scheduled in the past.", HttpStatus.BAD_REQUEST);
        }
        List<Meeting> conflictingMeetings = meetingRepository.findByTeacherAndTimeRange(
                teacher.get().getId(),
                meeting.getBeginning(),
                meeting.getBeginning().plusMinutes(meeting.getDuration())
        );
        if (!conflictingMeetings.isEmpty()) {
            throw new CustomException("Teacher has another meeting at this time.",
                    "Creation of meeting: Time conflict with another meeting.", HttpStatus.CONFLICT);
        }
        return meetingRepository.save(meeting);
    }

    @Override
    public Optional<Meeting> removeStudentMeeting(Long meetingId, Optional<User> student) {
        Optional<Meeting> meeting = meetingRepository.findById(meetingId);
        if (meeting.isEmpty()) {
            throw new CustomException("Meeting does not exist.",
                    "Removal of student from meeting: Meeting with id: " + meetingId
                            + " does not exist.", HttpStatus.NOT_FOUND);
        }
        if(student.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Removal of student from meeting: User does not exist.", HttpStatus.NOT_FOUND);
        }
        if(student.get().getRole() != Role.STUDENT){
            throw new CustomException("Only student can be removed from a meeting.",
                    "Removal of student from meeting: Only student can be removed from a meeting: " + meetingId + ".",
                    HttpStatus.BAD_REQUEST);
        }
        if (!Objects.equals(meeting.get().getStudent().getId(), student.get().getId())){
            throw new CustomException("User was not assigned to this meeting.",
                    "Removal of student from meeting: User was not assigned to this meeting: " + meetingId + ".",
                    HttpStatus.BAD_REQUEST);
        }
        meeting.get().setStudent(null);
        meetingRepository.save(meeting.get());
        return meeting;
    }

    @Override
    public List<Meeting> getCourseMeeting(Long courseId, Optional<User> student) {
        if(student.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Get course meeting: User does not exist.", HttpStatus.NOT_FOUND);
        }
        if(student.get().getRole() != Role.STUDENT){
            throw new CustomException("Only student can use this endpoint: /courseMeetings/{courseId}.",
                    "Get course meeting: Only student can use this endpoint: /courseMeetings/{courseId}.",
                    HttpStatus.BAD_REQUEST);
        }
        Optional<List<Meeting>> meetings = meetingRepository.findAllByCourse_IdAndStudentIsNull(courseId);
        return meetings.orElse(Collections.emptyList());
    }

    @Override
    public Meeting addStudentToMeeting(Long meetingId, Optional<User> student) {
        Optional<Meeting> meeting = meetingRepository.findById(meetingId);
        if (meeting.isEmpty()) {
            throw new CustomException("Meeting does not exist.",
                    "Adding student to meeting: Meeting with id: " + meetingId + " does not exist.",
                    HttpStatus.NOT_FOUND);
        }
        if(student.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Adding student to meeting: User does not exist.", HttpStatus.NOT_FOUND);
        }
        if(student.get().getRole() != Role.STUDENT){
            throw new CustomException("Only student can assign to meeting.",
                    "Adding student to meeting: Only student can assign to meeting.",
                    HttpStatus.BAD_REQUEST);
        }
        Meeting newMeeting = meeting.get();
        newMeeting.setStudent(student.get());
        return meetingRepository.save(newMeeting);
    }
}
