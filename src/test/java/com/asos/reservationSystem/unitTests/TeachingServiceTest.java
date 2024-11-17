package com.asos.reservationSystem.unitTests;

import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.TeachingRequest;
import com.asos.reservationSystem.domain.entities.TeachingRequestStatus;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.exception.CustomException;
import com.asos.reservationSystem.repositories.TeachingRequestRepository;
import com.asos.reservationSystem.services.impl.CourseServiceImpl;
import com.asos.reservationSystem.services.impl.TeachingServiceImpl;
import com.asos.reservationSystem.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeachingServiceTest {
    @Mock
    private TeachingRequestRepository teachingRequestRepository;
    @InjectMocks
    private TeachingServiceImpl teachingService;
    @Mock
    private CourseServiceImpl courseService;
    @Mock
    private UserServiceImpl userService;

    @Test
    void applyTeacherShouldCreateTeachingRequestWhenValid() {
        User studentUser = new User();
        studentUser.setId(1L);
        studentUser.setRole(Role.STUDENT);

        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setRole(Role.ADMIN);

        Optional<User> studentOptional = Optional.of(studentUser);
        Optional<User> adminOptional = Optional.of(adminUser);

        TeachingRequest teachingRequest = new TeachingRequest();
        teachingRequest.setTeacher(studentUser);
        teachingRequest.setStatus(TeachingRequestStatus.PENDING);
        teachingRequest.setDateTime(LocalDateTime.now());

        when(teachingRequestRepository.findAll()).thenReturn(Collections.singletonList(teachingRequest));

        teachingService.applyTeacher(studentOptional);

        List<TeachingRequest> requests = teachingService.getTeachingRequests(adminOptional);

        assertNotNull(requests);
        assertEquals(1, requests.size());
        TeachingRequest createdRequest = requests.get(0);
        assertEquals(studentUser, createdRequest.getTeacher());
        assertEquals(TeachingRequestStatus.PENDING, createdRequest.getStatus());
        assertNotNull(createdRequest.getDateTime());
    }



    @Test
    void applyTeacherShouldThrowExceptionWhenUserDoesNotExist() {
        Optional<User> optionalUser = Optional.empty();

        CustomException exception = assertThrows(CustomException.class, () ->
                teachingService.applyTeacher(optionalUser));

        assertEquals("User does not exist.", exception.getMessage());
    }

    @Test
    void applyTeacherShouldThrowExceptionWhenRoleIsNotStudent() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setRole(Role.TEACHER);

        Optional<User> optionalUser = Optional.of(mockUser);

        CustomException exception = assertThrows(CustomException.class, () ->
                teachingService.applyTeacher(optionalUser));

        assertEquals("Only student can apply for teacher status.", exception.getMessage());
    }

    @Test
    void applyTeacherShouldThrowExceptionWhenRequestAlreadyExists() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setRole(Role.STUDENT);

        Optional<User> optionalUser = Optional.of(mockUser);

        when(teachingRequestRepository.existsByTeacher(mockUser)).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () ->
                teachingService.applyTeacher(optionalUser));

        assertEquals("Teaching request already exists.", exception.getMessage());
    }

    @Test
    void getTeachingRequestsShouldReturnRequestsForAdmin() {
        User adminUser = new User();
        adminUser.setRole(Role.ADMIN);
        Optional<User> userOptional = Optional.of(adminUser);

        TeachingRequest request = new TeachingRequest();
        request.setTeacher(new User());
        request.setStatus(TeachingRequestStatus.PENDING);

        when(teachingRequestRepository.findAll()).thenReturn(Collections.singletonList(request));

        List<TeachingRequest> result = teachingService.getTeachingRequests(userOptional);

        assertEquals(1, result.size());
        assertEquals(request, result.get(0));
    }

    @Test
    void getTeachingRequestsShouldThrowExceptionForNonAdmin() {
        User studentUser = new User();
        studentUser.setRole(Role.STUDENT);
        Optional<User> userOptional = Optional.of(studentUser);

        CustomException exception = assertThrows(CustomException.class, () ->
                teachingService.getTeachingRequests(userOptional));

        assertEquals("Only admin can get teaching requests.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void getTeachingRequestsShouldThrowExceptionForMissingUser() {
        Optional<User> userOptional = Optional.empty();

        CustomException exception = assertThrows(CustomException.class, () ->
                teachingService.getTeachingRequests(userOptional));

        assertEquals("User does not exist.", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void updateTeachingRequestStatusShouldApproveRequest() {
        User admin = new User();
        admin.setRole(Role.ADMIN);

        User student = new User();
        student.setRole(Role.STUDENT);

        TeachingRequest teachingRequest = new TeachingRequest();
        teachingRequest.setId(1L);
        teachingRequest.setTeacher(student);
        teachingRequest.setStatus(TeachingRequestStatus.PENDING);

        when(teachingRequestRepository.findById(1L)).thenReturn(Optional.of(teachingRequest));
        when(courseService.getStudentCourses(Optional.of(student))).thenReturn(Collections.emptyList());

        teachingService.updateTeachingRequestStatus(1L, student, Optional.of(admin),
                TeachingRequestStatus.APPROVED);

        assertEquals(TeachingRequestStatus.APPROVED, teachingRequest.getStatus());
        Mockito.verify(teachingRequestRepository).save(teachingRequest);
        Mockito.verify(userService).setRole(student, Role.TEACHER);
    }

    @Test
    void updateTeachingRequestStatusShouldRejectRequest() {
        User admin = new User();
        admin.setRole(Role.ADMIN);

        User teacher = new User();
        teacher.setRole(Role.TEACHER);

        TeachingRequest teachingRequest = new TeachingRequest();
        teachingRequest.setId(1L);
        teachingRequest.setTeacher(teacher);
        teachingRequest.setStatus(TeachingRequestStatus.PENDING);

        when(teachingRequestRepository.findById(1L)).thenReturn(Optional.of(teachingRequest));
        when(courseService.getTeacherCourses(Optional.of(teacher))).thenReturn(Collections.emptyList());

        teachingService.updateTeachingRequestStatus(1L, teacher, Optional.of(admin),
                TeachingRequestStatus.REJECTED);

        assertEquals(TeachingRequestStatus.REJECTED, teachingRequest.getStatus());
        Mockito.verify(teachingRequestRepository).save(teachingRequest);
        Mockito.verify(userService).setRole(teacher, Role.STUDENT);
    }

    @Test
    void updateTeachingRequestStatusShouldThrowExceptionWhenAdminNotFound() {
        User student = new User();

        CustomException exception = assertThrows(CustomException.class, () ->
                teachingService.updateTeachingRequestStatus(1L, student, Optional.empty(),
                        TeachingRequestStatus.APPROVED));

        assertEquals("User does not exist.", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void updateTeachingRequestStatusShouldThrowExceptionWhenUserIsNotAdmin() {
        User student = new User();
        User nonAdmin = new User();
        nonAdmin.setRole(Role.STUDENT);

        CustomException exception = assertThrows(CustomException.class, () ->
                teachingService.updateTeachingRequestStatus(1L, student, Optional.of(nonAdmin),
                        TeachingRequestStatus.APPROVED));

        assertEquals("Only admin can update teaching request status.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void updateTeachingRequestStatusShouldThrowExceptionWhenRequestAlreadyApproved() {
        User admin = new User();
        admin.setRole(Role.ADMIN);

        User student = new User();

        TeachingRequest teachingRequest = new TeachingRequest();
        teachingRequest.setId(1L);
        teachingRequest.setTeacher(student);
        teachingRequest.setStatus(TeachingRequestStatus.APPROVED);

        when(teachingRequestRepository.findById(1L)).thenReturn(Optional.of(teachingRequest));

        CustomException exception = assertThrows(CustomException.class, () ->
                teachingService.updateTeachingRequestStatus(1L, student, Optional.of(admin),
                        TeachingRequestStatus.APPROVED));

        assertEquals("Teaching request already approved.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void updateTeachingRequestStatusShouldThrowExceptionWhenRequestNotFound() {
        User admin = new User();
        admin.setRole(Role.ADMIN);

        when(teachingRequestRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                teachingService.updateTeachingRequestStatus(1L, new User(), Optional.of(admin),
                        TeachingRequestStatus.APPROVED));

        assertEquals("Teaching request not found.", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}
