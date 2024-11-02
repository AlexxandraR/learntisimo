package com.asos.reservationSystem.services;

import com.asos.reservationSystem.domain.entities.Course;

import java.util.List;

public interface CourseService {
    List<Course> getAll();
    List<Course> getAllTeacherCourses(Long teacherId);
}