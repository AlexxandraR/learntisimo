package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.CourseDto;
import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.mappers.Mapper;
import com.asos.reservationSystem.services.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PostMapping (path = "/courseStudent")
    public List<CourseDto> getAllStudentCourses(@RequestBody String studentId) {
        return courseService.getAllStudentCourses(Long.parseLong(studentId.trim())).stream().map(courseMapper::mapToDto).
                collect(Collectors.toList());
    }
}
