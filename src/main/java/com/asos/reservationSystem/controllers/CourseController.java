package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.CourseDto;
import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.mappers.Mapper;
import com.asos.reservationSystem.services.CourseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class CourseController {
    private final CourseService courseService;

    private final Mapper<Course, CourseDto> courseMapper;

    public CourseController(CourseService courseService, Mapper<Course, CourseDto> courseMapper) {
        this.courseService = courseService;
        this.courseMapper = courseMapper;
    }
}
