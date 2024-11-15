package com.asos.reservationSystem.services;

import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.User;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    List<Course> getAll();

    Optional<Course> assignToCourse(Optional<User> user, Long courseId);
    Optional<Course> deleteFromCourse(Optional<User> user, Long courseId);
    List<Course> getTeacherCourses(Optional<User> user);
    List<Course> getStudentCourses(Optional<User> user);
    Course saveCourse(Optional<User> user, Course course);

    void removeCourse(Long courseId, Optional<User> user);
}
