package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.repositories.CourseRepository;
import com.asos.reservationSystem.services.CourseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CourseServiceImpl implements CourseService {
    private CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public List<Course> getAll() {
        return StreamSupport.stream(courseRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}
