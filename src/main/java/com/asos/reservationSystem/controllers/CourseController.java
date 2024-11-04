package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.CourseDto;
import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.mappers.Mapper;
import com.asos.reservationSystem.services.CourseService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class CourseController {
    private final CourseService courseService;

    private final Mapper<Course, CourseDto> courseMapper;

    public CourseController(CourseService courseService, Mapper<Course, CourseDto> courseMapper) {
        this.courseService = courseService;
        this.courseMapper = courseMapper;
    }

//    TODO: Rework response to return a response entity


    @GetMapping(path = "/course")
    public List<CourseDto> getAll(){
        List<Course> course = courseService.getAll();
        return course.stream().map(courseMapper::mapToDto).
                collect(Collectors.toList());
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
