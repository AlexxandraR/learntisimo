package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.CourseDto;
import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.exception.CustomException;
import com.asos.reservationSystem.mappers.Mapper;
import com.asos.reservationSystem.services.CourseService;
import com.asos.reservationSystem.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class CourseController {
    private final CourseService courseService;
    private final UserService userService;

    private final Mapper<Course, CourseDto> courseMapper;

    private final Logger logger;

    public CourseController(CourseService courseService, UserService userService, Mapper<Course, CourseDto> courseMapper) {
        this.courseService = courseService;
        this.userService = userService;
        this.courseMapper = courseMapper;
        this.logger = LoggerFactory.getLogger(CourseController.class);
    }

//    TODO: Rework response to return a response entity


    @GetMapping(path = "/course")
    public ResponseEntity<List<CourseDto>> getAll(){
        try {
            List<Course> courses = courseService.getAll();
            List<CourseDto> courseDtos = courses.stream().map(courseMapper::mapToDto).collect(Collectors.toList());
            logger.info("Successfully retrieved all courses at: " + LocalDateTime.now());
            return new ResponseEntity<>(courseDtos, HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error retrieving courses: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error retrieving courses: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping(path = "/assignToCourse/{courseId}")
    public ResponseEntity<CourseDto> assignToCourse(Principal connectedCustomer, @PathVariable Long courseId) {
        try {
            userService.checkRole(connectedCustomer, Role.STUDENT);
            Optional<User> user = userService.getUser(connectedCustomer);
            Optional<Course> course = courseService.assignToCourse(user, courseId);
            logger.info("User with id: " + user.get().getId() + " assigned to course with id: " + course.get().getId()
                    + " at: " + LocalDateTime.now());
            return course.map(courseEntity -> {
                CourseDto courseDto = courseMapper.mapToDto(courseEntity);
                return new ResponseEntity<>(courseDto, HttpStatus.OK);
            }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (CustomException e) {
            logger.error("Error assigning user to course: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error assigning user to course: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/deleteFromCourse/{courseId}")
    public ResponseEntity<CourseDto> deleteFromCourse(Principal connectedCustomer, @PathVariable Long courseId) {
        try {
            userService.checkRole(connectedCustomer, Role.STUDENT);
            Optional<User> user = userService.getUser(connectedCustomer);
            Optional<Course> course = courseService.deleteFromCourse(user, courseId);
            logger.info("User with id: " + user.get().getId() + " removed from course with id: " + course.get().getId()
                    + " at: " + LocalDateTime.now());
            return course.map(courseEntity -> {
                CourseDto courseDto = courseMapper.mapToDto(courseEntity);
                return new ResponseEntity<>(courseDto, HttpStatus.OK);
            }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (CustomException e) {
            logger.error("Error removing user from course: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error removing user from course: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/courseTeacher")
    public ResponseEntity<List<CourseDto>> getAllTeacherCourses(@RequestBody String teacherId, Principal connectedUser) {
        try {
            userService.checkRole(connectedUser, Role.TEACHER);
            userService.checkConnectedToProvidedUser(connectedUser, teacherId);
            List<Course> courses = courseService.getAllTeacherCourses(Long.parseLong(teacherId.trim()));
            List<CourseDto> courseDtos = courses.stream().map(courseMapper::mapToDto).collect(Collectors.toList());
            logger.info("Successfully retrieved all courses for teacher with id: " + teacherId + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(courseDtos, HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error retrieving courses for teacher: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error retrieving courses for teacher: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/courseTeacherEmail")
    public ResponseEntity<List<CourseDto>> getAllTeacherCoursesByEmail(@RequestBody String teacherEmail, Principal connectedUser) {
        try {
            userService.checkRole(connectedUser, Role.TEACHER);
            userService.checkConnectedToProvidedUserByEmail(connectedUser, teacherEmail.trim());
            List<Course> courses = courseService.getAllTeacherCoursesByEmail(teacherEmail);
            List<CourseDto> courseDtos = courses.stream().map(courseMapper::mapToDto).collect(Collectors.toList());
            logger.info("Successfully retrieved all courses for teacher with email: " + teacherEmail + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(courseDtos, HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error retrieving courses for teacher: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error retrieving courses for teacher: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/courseStudent")
    public ResponseEntity<List<CourseDto>> getAllStudentCourses(@RequestBody String studentId, Principal connectedUser) {
        try {
            userService.checkRole(connectedUser, Role.STUDENT);
            userService.checkConnectedToProvidedUser(connectedUser, studentId);
            List<Course> courses = courseService.getAllStudentCourses(Long.parseLong(studentId.trim()));
            List<CourseDto> courseDtos = courses.stream().map(courseMapper::mapToDto).collect(Collectors.toList());
            logger.info("Successfully retrieved all courses for student with id: " + studentId + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(courseDtos, HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error retrieving courses for student: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error retrieving courses for student: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/createCourse")
    public ResponseEntity<CourseDto> saveCourse(@RequestBody CourseDto courseDto, Principal connectedUser) {
        try {
            userService.checkRole(connectedUser, Role.TEACHER);
            userService.checkConnectedToProvidedUserByEmail(connectedUser, courseDto.getTeacher().getEmail());
            Course newCourse = courseService.saveCourse(courseMapper.mapFromDto(courseDto));
            CourseDto newCourseDto = courseMapper.mapToDto(newCourse);
            logger.info("Successfully created course with id: " + newCourse.getId() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(newCourseDto, HttpStatus.CREATED);
        } catch (CustomException e) {
            logger.error("Error creating course: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (Exception e) {
            logger.error("Unexpected error creating course: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/removeCourse/{courseId}")
    public ResponseEntity<Void> removeCourse(@PathVariable String courseId, Principal connectedUser) {
        try {
            courseService.removeCourse(Long.parseLong(courseId.trim()), connectedUser);
            logger.info("Successfully removed course with id: " + courseId + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomException e) {
            logger.error("Error removing course: " + e.getLoggingMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(e.getStatus());
        } catch (NoSuchElementException e) {
            logger.error("Course not found: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("Unexpected error removing course: " + e.getMessage() + " at: " + LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
