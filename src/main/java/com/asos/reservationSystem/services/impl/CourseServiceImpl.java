package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.exception.CustomException;
import com.asos.reservationSystem.repositories.CourseRepository;
import com.asos.reservationSystem.services.CourseService;
import com.asos.reservationSystem.services.MeetingService;
import com.asos.reservationSystem.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final MeetingService meetingService;
    private final UserService userService;

    public CourseServiceImpl(CourseRepository courseRepository, MeetingService meetingService, UserService userService) {
        this.courseRepository = courseRepository;
        this.meetingService = meetingService;
        this.userService = userService;
    }

    @Override
    public List<Course> getAll() {
        return StreamSupport.stream(courseRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Course> assignToCourse(Optional<User> user, Long courseId) {
        try {
            Optional<Course> course = courseRepository.findById(courseId);
            if (course.isEmpty()) {
                throw new CustomException("Course does not exist.",
                        "Assignment to course: Course with id: " + courseId + " does not exist.", HttpStatus.NOT_FOUND);
            }
            if (user.isEmpty()) {
                throw new CustomException("User does not exist.",
                        "Assignment to course: User does not exist.", HttpStatus.NOT_FOUND);
            }
            if (user.get().getRole() != Role.STUDENT) {
                throw new CustomException("Only student can assign to a course.",
                        "Assignment to course: Only student can assign to a course: " + courseId + ".", HttpStatus.BAD_REQUEST);
            }
            if (course.get().getStudents().stream().noneMatch(student -> student.getId().equals(user.get().getId()))) {
                course.get().getStudents().add(user.get());
                courseRepository.save(course.get());
            } else {
                throw new CustomException("User has already been assigned to this course.",
                        "Assignment to course: User has already been assigned to this course: " + courseId + ".", HttpStatus.BAD_REQUEST);
            }
            return course;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while assigning user to course.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Optional<Course> deleteFromCourse(Optional<User> user, Long courseId) {
        try {
            Optional<Course> course = courseRepository.findById(courseId);
            if (course.isEmpty()) {
                throw new CustomException("Course does not exist.",
                        "Removal from course: Course with id: " + courseId + " does not exist.", HttpStatus.NOT_FOUND);
            }
            if (user.isEmpty()) {
                throw new CustomException("User does not exist.",
                        "Removal from course: User does not exist.", HttpStatus.NOT_FOUND);
            }
            if (user.get().getRole() != Role.STUDENT) {
                throw new CustomException("Only student can be removed from a course.",
                        "Removal from course: Only student can be removed from a course: " + courseId + ".", HttpStatus.BAD_REQUEST);
            }
            if (course.get().getStudents().stream().noneMatch(student -> student.getId().equals(user.get().getId()))) {
                throw new CustomException("User was not assigned to this course.",
                        "Removal from course: User was not assigned to this course: " + courseId + ".", HttpStatus.BAD_REQUEST);
            } else {
                meetingService.removeStudentFromCourseMeetings(courseId, user.get().getId());
                course.get().getStudents().remove(user.get());
                courseRepository.save(course.get());
            }
            return course;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while removing user from course.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Course> getAllTeacherCourses(Long teacherId) {
        var courses = courseRepository.findAllByTeacher_Id(teacherId);
        return courses.orElse(Collections.emptyList());
    }

    public List<Course> getAllStudentCourses(Long studentId) {
        var courses = courseRepository.findAllByStudents_Id(studentId);
        return courses.orElse(Collections.emptyList());
    }

    @Override
    public Course saveCourse(Course course) {
        if (course == null) {
            throw new CustomException("Course is null.",
                    "Saving course: Course is null.", HttpStatus.BAD_REQUEST);
        } else if (course.getTeacher() == null) {
            throw new CustomException("Course teacher is null.",
                    "Saving course: Course teacher is null.", HttpStatus.BAD_REQUEST);
        }
        try {
            return courseRepository.save(course);
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while saving course.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void removeCourse(Long courseId, Principal connectedUser) {
        try {
            Optional<Course> course = courseRepository.findById(courseId);
            if (course.isEmpty()) {
                throw new CustomException("Course does not exist.",
                        "Removal of course: Course with id: " + courseId + " does not exist.", HttpStatus.NOT_FOUND);
            }
            userService.checkRole(connectedUser, Role.TEACHER);
            userService.checkConnectedToProvidedUserByEmail(connectedUser, course.get().getTeacher().getEmail());
            courseRepository.deleteById(courseId);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while removing course.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Course> getAllTeacherCoursesByEmail(String teacherEmail) {
        var courses = courseRepository.findAllByTeacher_Email(teacherEmail);
        return courses.orElse(Collections.emptyList());
    }
}
