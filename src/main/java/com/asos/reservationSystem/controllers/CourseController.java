package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.CourseDto;
import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.mappers.Mapper;
import com.asos.reservationSystem.services.CourseService;
import com.asos.reservationSystem.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class CourseController {
    private final CourseService courseService;
    private final UserService userService;

    private final Mapper<Course, CourseDto> courseMapper;

    private final Logger logger;

    public CourseController(CourseService courseService, UserService userService, Mapper<Course, CourseDto>
            courseMapper) {
        this.courseService = courseService;
        this.userService = userService;
        this.courseMapper = courseMapper;
        this.logger = LoggerFactory.getLogger(CourseController.class);
    }

    @GetMapping(path = "/course")
    public List<CourseDto> getAll() {
        List<Course> course = courseService.getAll();
        return course.stream().map(courseMapper::mapToDto).
                collect(Collectors.toList());
    }

    @PatchMapping(path = "/assignToCourse/{courseId}")
    public ResponseEntity<CourseDto> assignToCourse(Principal connectedUser, @PathVariable Long courseId) {
        Optional<User> user = userService.getUser(connectedUser);
        Optional<Course> course = courseService.assignToCourse(user, courseId);
        logger.info("Assignment to course: User with id: " + user.get().getId() + " assigned to course with id: "
                + course.get().getId() + " at: " + LocalDateTime.now());
        return course.map(courseEntity -> {
            CourseDto courseDto = courseMapper.mapToDto(courseEntity);
            return new ResponseEntity<>(courseDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(path = "/deleteFromCourse/{courseId}")
    public ResponseEntity<CourseDto> deleteFromCourse(Principal connectedUser, @PathVariable Long courseId) {
        Optional<User> user = userService.getUser(connectedUser);
        Optional<Course> course = courseService.deleteFromCourse(user, courseId);
        logger.info("Removal from course: User with id: " + user.get().getId() + " removed from course with id: "
                + course.get().getId() + " at: " + LocalDateTime.now());
        return course.map(courseEntity -> {
            CourseDto courseDto = courseMapper.mapToDto(courseEntity);
            return new ResponseEntity<>(courseDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(path = "/teacherCourses")
    public List<CourseDto> getTeacherCourses(Principal connectedUser) {
        Optional<User> teacher = userService.getUser(connectedUser);
        List<Course> courses = courseService.getTeacherCourses(teacher);
        logger.info("Get teacher courses: Successfully retrieved all courses for teacher with id: "
                + teacher.get().getId() + " at: " + LocalDateTime.now());
        return courses.stream().map(courseMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/studentCourses")
    public List<CourseDto> getStudentCourses(Principal connectedUser) {
        Optional<User> student = userService.getUser(connectedUser);
        List<Course> courses = courseService.getStudentCourses(student);
        logger.info("Get student courses: Successfully retrieved all courses for student with id: "
                + student.get().getId() + " at: " + LocalDateTime.now());
        return courses.stream().map(courseMapper::mapToDto).
                collect(Collectors.toList());
    }

    @PostMapping(path = "/createCourse")
    public ResponseEntity<CourseDto> saveCourse(@RequestBody CourseDto courseDto, Principal connectedUser) {
        Optional<User> teacher = userService.getUser(connectedUser);
        Course newCourse = courseService.saveCourse(teacher, courseMapper.mapFromDto(courseDto));
        CourseDto newCourseDto = courseMapper.mapToDto(newCourse);
        logger.info("Saving course: Successfully created course with id: " + newCourse.getId() + " at: "
                + LocalDateTime.now());
        return new ResponseEntity<>(newCourseDto, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/removeCourse/{courseId}")
    public ResponseEntity<Void> removeCourse(@PathVariable Long courseId, Principal connectedUser) {
        Optional<User> teacher = userService.getUser(connectedUser);
        courseService.removeCourse(courseId, teacher);
        logger.info("Removal of course: Successfully removed course with id: " + courseId + " at: "
                + LocalDateTime.now());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
