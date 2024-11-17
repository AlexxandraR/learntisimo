package com.asos.reservationSystem.unitTests;

import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.exception.CustomException;
import com.asos.reservationSystem.repositories.MeetingRepository;
import com.asos.reservationSystem.services.impl.MeetingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MeetingServiceTest {
    @Mock
    private MeetingRepository meetingRepository;
    @InjectMocks
    private MeetingServiceImpl meetingService;
    private User teacher;
    private User student;
    private Meeting meeting;

    @BeforeEach
    void setup() {
        teacher = new User();
        teacher.setId(1L);
        teacher.setRole(Role.TEACHER);

        student = new User();
        student.setId(2L);
        student.setRole(Role.STUDENT);

        meeting = new Meeting();
        meeting.setId(1L);
        meeting.setTeacher(teacher);
        meeting.setBeginning(LocalDateTime.now().plusDays(1));
        meeting.setDuration(60);
    }

    @Test
    void getTeacherMeetingsTeacherDoesNotExistThrowsException() {
        Optional<User> teacher = Optional.empty();
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.getTeacherMeetings(teacher));
        assertEquals("User does not exist.", exception.getMessage());
    }

    @Test
    void getTeacherMeetingsNotATeacherThrowsException() {
        student.setRole(Role.STUDENT);
        Optional<User> notTeacher = Optional.of(student);
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.getTeacherMeetings(notTeacher));
        assertEquals("Only teacher can use this endpoint: /teacherMeetings.", exception.getMessage());
    }

    @Test
    void getTeacherMeetingsValidTeacherReturnsMeetings() {
        List<Meeting> meetings = List.of(meeting);
        Mockito.when(meetingRepository.findByTeacherId(teacher.getId())).thenReturn(meetings);
        List<Meeting> result = meetingService.getTeacherMeetings(Optional.of(teacher));
        assertEquals(1, result.size());
        assertEquals(meeting, result.get(0));
    }

    @Test
    void getStudentMeetingsStudentDoesNotExistThrowsException() {
        Optional<User> student = Optional.empty();
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.getStudentMeetings(student));
        assertEquals("User does not exist.", exception.getMessage());
    }

    @Test
    void getStudentMeetingsNotAStudentThrowsException() {
        teacher.setRole(Role.TEACHER);
        Optional<User> notStudent = Optional.of(teacher);
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.getStudentMeetings(notStudent));
        assertEquals("Only student can use this endpoint: /studentMeetings.", exception.getMessage());
    }

    @Test
    void getStudentMeetingsValidStudentReturnsMeetings() {
        List<Meeting> meetings = List.of(meeting);
        Mockito.when(meetingRepository.findByStudentId(student.getId())).thenReturn(meetings);
        List<Meeting> result = meetingService.getStudentMeetings(Optional.of(student));
        assertEquals(1, result.size());
        assertEquals(meeting, result.get(0));
    }

    @Test
    void removeMeetingMeetingDoesNotExistThrowsException() {
        Mockito.when(meetingRepository.findById(meeting.getId())).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.removeMeeting(meeting.getId(), Optional.of(teacher)));
        assertEquals("Meeting does not exist.", exception.getMessage());
    }

    @Test
    void removeMeetingNotATeacherThrowsException() {
        Mockito.when(meetingRepository.findById(meeting.getId())).thenReturn(Optional.of(meeting));
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.removeMeeting(meeting.getId(), Optional.of(student)));
        assertEquals("Only teacher can delete meeting.", exception.getMessage());
    }

    @Test
    void removeMeetingValidTeacherRemovesMeeting() {
        Mockito.when(meetingRepository.findById(meeting.getId())).thenReturn(Optional.of(meeting));
        meetingService.removeMeeting(meeting.getId(), Optional.of(teacher));
        Mockito.verify(meetingRepository, Mockito.times(1)).deleteById(meeting.getId());
    }

    @Test
    void createMeetingNullMeetingThrowsException() {
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.createMeeting(null, Optional.of(teacher)));
        assertEquals("Meeting is null.", exception.getMessage());
    }

    @Test
    void createMeetingMeetingInPastThrowsException() {
        meeting.setBeginning(LocalDateTime.now().minusDays(1));
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.createMeeting(meeting, Optional.of(teacher)));
        assertEquals("Cannot schedule meeting in the past.", exception.getMessage());
    }

    @Test
    void createMeetingValidTeacherAndMeetingCreatesMeeting() {
        Mockito.when(meetingRepository.save(meeting)).thenReturn(meeting);
        Meeting result = meetingService.createMeeting(meeting, Optional.of(teacher));
        assertEquals(meeting, result);
        Mockito.verify(meetingRepository, Mockito.times(1)).save(meeting);
    }

    @Test
    void removeStudentMeetingMeetingDoesNotExistThrowsException() {
        Mockito.when(meetingRepository.findById(meeting.getId())).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.removeStudentMeeting(meeting.getId(), Optional.of(student)));
        assertEquals("Meeting does not exist.", exception.getMessage());
    }

    @Test
    void removeStudentMeetingUserNotAssignedToMeetingThrowsException() {
        meeting.setStudent(new User());
        Mockito.when(meetingRepository.findById(meeting.getId())).thenReturn(Optional.of(meeting));
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.removeStudentMeeting(meeting.getId(), Optional.of(student)));
        assertEquals("User was not assigned to this meeting.", exception.getMessage());
    }

    @Test
    void removeStudentMeetingValidStudentRemovesStudent() {
        meeting.setStudent(student);
        Mockito.when(meetingRepository.findById(meeting.getId())).thenReturn(Optional.of(meeting));
        Mockito.when(meetingRepository.save(meeting)).thenReturn(meeting);

        Optional<Meeting> result = meetingService.removeStudentMeeting(meeting.getId(), Optional.of(student));
        assertTrue(result.isPresent());
        assertNull(result.get().getStudent());
        Mockito.verify(meetingRepository, Mockito.times(1)).save(meeting);
    }

    @Test
    void getCourseMeetingUserDoesNotExistThrowsException() {
        Optional<User> student = Optional.empty();
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.getCourseMeeting(1L, student));
        assertEquals("User does not exist.", exception.getMessage());
    }

    @Test
    void getCourseMeetingNotAStudentThrowsException() {
        Optional<User> notStudent = Optional.of(teacher);
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.getCourseMeeting(1L, notStudent));
        assertEquals("Only student can use this endpoint: /courseMeetings/{courseId}.",
                exception.getMessage());
    }

    @Test
    void getCourseMeetingValidStudentReturnsMeetings() {
        List<Meeting> meetings = List.of(meeting);
        Mockito.when(meetingRepository.findAllByCourse_IdAndStudentIsNull(1L))
                .thenReturn(Optional.of(meetings));

        List<Meeting> result = meetingService.getCourseMeeting(1L, Optional.of(student));
        assertEquals(1, result.size());
        assertEquals(meeting, result.get(0));
    }

    @Test
    void addStudentToMeetingMeetingDoesNotExistThrowsException() {
        Mockito.when(meetingRepository.findById(meeting.getId())).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.addStudentToMeeting(meeting.getId(), Optional.of(student)));
        assertEquals("Meeting does not exist.", exception.getMessage());
    }

    @Test
    void addStudentToMeetingStudentHasTimeConflictThrowsException() {
        Mockito.when(meetingRepository.findById(meeting.getId())).thenReturn(Optional.of(meeting));
        Mockito.when(meetingRepository.findByStudentAndTimeRange(student.getId(),
                meeting.getBeginning(), meeting.getBeginning().plusMinutes(
                        meeting.getDuration()))).thenReturn(List.of(new Meeting()));

        CustomException exception = assertThrows(CustomException.class, () ->
                meetingService.addStudentToMeeting(meeting.getId(), Optional.of(student)));
        assertEquals("Student has another meeting at this time.", exception.getMessage());
    }

    @Test
    void addStudentToMeetingValidStudentAddsStudentToMeeting() {
        Mockito.when(meetingRepository.findById(meeting.getId())).thenReturn(Optional.of(meeting));
        Mockito.when(meetingRepository.save(meeting)).thenReturn(meeting);

        Meeting result = meetingService.addStudentToMeeting(meeting.getId(), Optional.of(student));
        assertNotNull(result.getStudent());
        assertEquals(student, result.getStudent());
        Mockito.verify(meetingRepository, Mockito.times(1)).save(meeting);
    }
}
