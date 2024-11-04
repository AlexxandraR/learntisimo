package com.asos.reservationSystem.services;

import com.asos.reservationSystem.domain.entities.Course;

import java.util.Collection;
import java.util.List;

public interface CourseService {
    List<Course> getAll();
    List<Course> getAllTeacherCourses(Long teacherId);

    List<Course> getAllStudentCourses(Long l);

    void saveCourse(Course course);

    void removeCourse(Long courseId);
}
