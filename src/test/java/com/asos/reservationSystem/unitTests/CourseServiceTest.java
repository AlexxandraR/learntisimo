package com.asos.reservationSystem.unitTests;

import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.exception.CustomException;
import com.asos.reservationSystem.repositories.CourseRepository;
import com.asos.reservationSystem.repositories.MeetingRepository;
import com.asos.reservationSystem.services.impl.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;
    @InjectMocks
    private CourseServiceImpl courseService;
    @Mock
    private MeetingRepository meetingRepository;
    private User teacher;
    private User student;
    private Course course;

    @BeforeEach
    void setup() {
        teacher = new User();
        teacher.setId(1L);
        teacher.setRole(Role.TEACHER);

        student = new User();
        student.setId(2L);
        student.setRole(Role.STUDENT);

        course = new Course();
        course.setId(1L);
        course.setName("Test Course");
        course.setTeacher(teacher);
        course.setStudents(new ArrayList<>());
    }

    @Test
    void getAllCoursesReturnsAllCourses() {
        List<Course> courses = Collections.singletonList(course);
        when(courseRepository.findAll()).thenReturn(courses);

        List<Course> result = courseService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Test Course", result.get(0).getName());
    }

    @Test
    void assignToCourseUserDoesNotExistThrowsException() {
        Optional<User> emptyUser = Optional.empty();

        Course course = new Course();
        course.setId(1L);
        course.setName("Test Course");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.assignToCourse(emptyUser, 1L));

        assertEquals("User does not exist.", exception.getMessage());
    }

    @Test
    void assignToCourseCourseDoesNotExistThrowsException() {
        Optional<User> user = Optional.of(student);
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.assignToCourse(user, 1L));

        assertEquals("Course does not exist.", exception.getMessage());
    }

    @Test
    void assignToCourseUserNotStudentThrowsException() {
        teacher.setRole(Role.TEACHER);
        Optional<User> user = Optional.of(teacher);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.assignToCourse(user, 1L));

        assertEquals("Only student can assign to a course.", exception.getMessage());
    }

    @Test
    void assignToCourseAlreadyAssignedThrowsException() {
        course.getStudents().add(student);
        Optional<User> user = Optional.of(student);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.assignToCourse(user, 1L));

        assertEquals("User has already been assigned to this course.", exception.getMessage());
    }

    @Test
    void assignToCourseSuccessfulAssignment() {
        Optional<User> user = Optional.of(student);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Optional<Course> result = courseService.assignToCourse(user, 1L);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getStudents().size());
        assertEquals("Test Course", result.get().getName());
    }

    @Test
    void deleteFromCourseUserDoesNotExistThrowsException() {
        Optional<User> emptyUser = Optional.empty();

        Course course = new Course();
        course.setId(1L);
        course.setName("Test Course");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.deleteFromCourse(emptyUser, 1L));

        assertEquals("User does not exist.", exception.getMessage());
    }

    @Test
    void deleteFromCourseCourseDoesNotExistThrowsException() {
        Optional<User> user = Optional.of(student);
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.deleteFromCourse(user, 1L));

        assertEquals("Course does not exist.", exception.getMessage());
    }

    @Test
    void deleteFromCourseNotStudentThrowsException() {
        teacher.setRole(Role.TEACHER);
        Optional<User> user = Optional.of(teacher);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.deleteFromCourse(user, 1L));

        assertEquals("Only student can be removed from a course.", exception.getMessage());
    }

    @Test
    void deleteFromCourseUserNotAssignedThrowsException() {
        Optional<User> user = Optional.of(student);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.deleteFromCourse(user, 1L));

        assertEquals("User was not assigned to this course.", exception.getMessage());
    }

    @Test
    void deleteFromCourseSuccessfulRemoval() {
        User student = new User();
        student.setId(2L);
        student.setRole(Role.STUDENT);

        Course course = new Course();
        course.setId(1L);
        course.setName("Test Course");
        List<User> list = new ArrayList<>();
        list.add(student);
        course.setStudents(list);
        Optional<User> user = Optional.of(student);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));



        Optional<Course> result = courseService.deleteFromCourse(user, 1L);

        assertTrue(result.isPresent());
        assertEquals(0, result.get().getStudents().size());
        verify(courseRepository, times(1)).save(course);
    }


    @Test
    void getTeacherCoursesUserDoesNotExistThrowsException() {
        Optional<User> emptyUser = Optional.empty();

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.getTeacherCourses(emptyUser));

        assertEquals("User does not exist.", exception.getMessage());
    }

    @Test
    void getTeacherCoursesNotATeacherThrowsException() {
        student.setRole(Role.STUDENT);
        Optional<User> notTeacher = Optional.of(student);

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.getTeacherCourses(notTeacher));

        assertEquals("Only teacher can use this endpoint: /teacherCourses.", exception.getMessage());
    }

    @Test
    void getTeacherCoursesSuccessful() {
        List<Course> courses = Collections.singletonList(course);
        when(courseRepository.findByTeacherId(1L)).thenReturn(courses);
        Optional<User> teacherUser = Optional.of(teacher);

        List<Course> result = courseService.getTeacherCourses(teacherUser);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Course", result.get(0).getName());
    }

    @Test
    void getStudentCoursesUserDoesNotExistThrowsException() {
        Optional<User> emptyUser = Optional.empty();

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.getStudentCourses(emptyUser));

        assertEquals("User does not exist.", exception.getMessage());
    }

    @Test
    void getStudentCoursesNotAStudentThrowsException() {
        teacher.setRole(Role.TEACHER);
        Optional<User> notStudent = Optional.of(teacher);

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.getStudentCourses(notStudent));

        assertEquals("Only student can use this endpoint: /studentCourses.", exception.getMessage());
    }

    @Test
    void getStudentCoursesSuccessful() {
        course.getStudents().add(student);
        List<Course> courses = Collections.singletonList(course);
        when(courseRepository.findByStudentsId(2L)).thenReturn(courses);
        Optional<User> studentUser = Optional.of(student);

        List<Course> result = courseService.getStudentCourses(studentUser);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Course", result.get(0).getName());
    }

    @Test
    void saveCourseUserDoesNotExistThrowsException() {
        Optional<User> emptyUser = Optional.empty();

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.saveCourse(emptyUser, course));

        assertEquals("Teacher does not exist.", exception.getMessage());
    }

    @Test
    void saveCourseNotATeacherThrowsException() {
        student.setRole(Role.STUDENT);
        Optional<User> user = Optional.of(student);

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.saveCourse(user, course));

        assertEquals("Only teacher can create course.", exception.getMessage());
    }

    @Test
    void saveCourseNullCourseThrowsException() {
        Optional<User> user = Optional.of(teacher);

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.saveCourse(user, null));

        assertEquals("Course is null.", exception.getMessage());
    }

    @Test
    void saveCourseSuccessful() {
        Optional<User> user = Optional.of(teacher);
        when(courseRepository.save(course)).thenReturn(course);

        Course result = courseService.saveCourse(user, course);

        assertNotNull(result);
        assertEquals("Test Course", result.getName());
    }

    @Test
    void removeCourseCourseDoesNotExistThrowsException() {
        Optional<User> user = Optional.of(teacher);
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.removeCourse(1L, user));

        assertEquals("Course does not exist.", exception.getMessage());
    }

    @Test
    void removeCourseUserDoesNotExistThrowsException() {
        Course course = new Course();
        course.setId(1L);
        course.setName("Test Course");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Optional<User> emptyUser = Optional.empty();

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.removeCourse(1L, emptyUser));

        assertEquals("Teacher does not exist.", exception.getMessage());
    }

    @Test
    void removeCourseNotATeacherThrowsException() {
        Course course = new Course();
        course.setId(1L);
        course.setName("Test Course");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        student.setRole(Role.STUDENT);
        Optional<User> user = Optional.of(student);

        CustomException exception = assertThrows(CustomException.class, () ->
                courseService.removeCourse(1L, user));

        assertEquals("Only teacher can delete course.", exception.getMessage());
    }

    @Test
    void removeCourseSuccessful() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.removeCourse(1L, Optional.of(teacher));

        verify(courseRepository).deleteById(1L);
    }
}
