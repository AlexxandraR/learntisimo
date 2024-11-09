package com.asos.reservationSystem.services;

import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.User;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    List<Course> getAll();

    Optional<Course> assignToCourse(Optional<User> user, Long courseId);
    Optional<Course> deleteFromCourse(Optional<User> user, Long courseId);
    List<Course> getAllTeacherCourses(Long teacherId);

    List<Course> getAllStudentCourses(Long l);

    Course saveCourse(Course course);

    void removeCourse(Long courseId);

    List<Course> getAllTeacherCoursesByEmail(String teacherEmail);
}
