package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.CourseDto;
import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.User;
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
    public List<CourseDto> getAll(){
        List<Course> course = courseService.getAll();
        return course.stream().map(courseMapper::mapToDto).
                collect(Collectors.toList());
    }

    @PatchMapping(path = "/assignToCourse/{courseId}")
    public ResponseEntity<CourseDto> assignToCourse(Principal connectedCustomer, @PathVariable Long courseId){
        Optional<User> user = userService.getUser(connectedCustomer);
        Optional<Course> course = courseService.assignToCourse(user, courseId);
        logger.info("User with id: " + user.get().getId() + " assigned to course with id: " + course.get().getId()
                + " at: " + LocalDateTime.now());
        return course.map(courseEntity -> {
            CourseDto courseDto = courseMapper.mapToDto(courseEntity);
            return new ResponseEntity<>(courseDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(path = "/deleteFromCourse/{courseId}")
    public ResponseEntity<CourseDto> deleteFromCourse(Principal connectedCustomer, @PathVariable Long courseId){
        Optional<User> user = userService.getUser(connectedCustomer);
        Optional<Course> course = courseService.deleteFromCourse(user, courseId);
        logger.info("User with id: " + user.get().getId() + " removed from course with id: " + course.get().getId()
                + " at: " + LocalDateTime.now());
        return course.map(courseEntity -> {
            CourseDto courseDto = courseMapper.mapToDto(courseEntity);
            return new ResponseEntity<>(courseDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping (path = "/courseTeacher")
    public List<CourseDto> getAllTeacherCourses(@RequestBody String teacherId){
        return courseService.getAllTeacherCourses(Long.parseLong(teacherId.trim())).stream().map(courseMapper::mapToDto).
                collect(Collectors.toList());
    }

    @PostMapping (path = "/courseTeacherEmail")
    public List<CourseDto> getAllTeacherCoursesByEmail(@RequestBody String teacherEmail){
        return courseService.getAllTeacherCoursesByEmail(teacherEmail).stream().map(courseMapper::mapToDto).
                collect(Collectors.toList());
    }

    @PostMapping (path = "/courseStudent")
    public List<CourseDto> getAllStudentCourses(@RequestBody String studentId) {
        return courseService.getAllStudentCourses(Long.parseLong(studentId.trim())).stream().map(courseMapper::mapToDto).
                collect(Collectors.toList());
    }

    @PostMapping(path = "/createCourse")
    public void saveCourse(@RequestBody CourseDto courseDto){
        courseService.saveCourse(courseMapper.mapFromDto(courseDto));
    }

    @DeleteMapping(path = "/removeCourse/{courseId}")
    public ResponseEntity<Void> removeCourse(@PathVariable String courseId) {
        try {
            courseService.removeCourse(Long.parseLong(courseId.trim()));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
