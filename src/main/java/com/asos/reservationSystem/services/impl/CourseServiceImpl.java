package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.exception.CustomException;
import com.asos.reservationSystem.repositories.CourseRepository;
import com.asos.reservationSystem.services.CourseService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public List<Course> getAll() {
        return StreamSupport.stream(courseRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Course> assignToCourse(Optional<User> user, Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if(course.isEmpty()){
            throw new CustomException("Course does not exist.",
                    "Assignment to course: Course with id: " + courseId + " does not exist.");
        }
        if(user.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Assignment to course: User does not exist.");
        }
        if (course.get().getStudents().stream().noneMatch(student -> student.getId().equals(user.get().getId()))){
            course.get().getStudents().add(user.get());
            courseRepository.save(course.get());
        }
        else{
            throw new CustomException("User has already been assigned to this course.",
                    "Assignment to course: User has already been assigned to this course: " + courseId + ".");
        }
        return course;
    }

    public List<Course> getAllTeacherCourses(Long teacherId) {
        var courses = courseRepository.findAllByTeacher_Id(teacherId);
        if (courses.isEmpty()) {
            System.out.println("No courses found for teacher with id " + teacherId);
            return Collections.emptyList();
        } else {
            return courses.get();
        }
    }

    public List<Course> getAllStudentCourses(Long studentId) {
        var courses = courseRepository.findAllByStudents_Id(studentId);
        if (courses.isEmpty()) {
            System.out.println("No courses found for teacher with id " + studentId);
            return Collections.emptyList();
        } else {
            return courses.get();
        }
    }

    @Override
    public void saveCourse(Course course) {
//        TODO: handle same course being created, handle missing values
        if (course == null) {
            throw new AccessDeniedException("Access Denied: Course is null");
        } else if (course.getTeacher() == null) {
            throw new AccessDeniedException("Access Denied: Course teacher is null");
        }
        courseRepository.save(course);
    }

    @Override
    public void removeCourse(Long courseId) {
//        TODO: handle course not found
//        TODO: handle User removing course they are not teaching
        courseRepository.deleteById(courseId);
    }

    @Override
    public List<Course> getAllTeacherCoursesByEmail(String teacherEmail) {
        var courses = courseRepository.findAllByTeacher_Email(teacherEmail);
        if (courses.isEmpty()) {
            System.out.println("No courses found for teacher with email " + teacherEmail);
            return Collections.emptyList();
        } else {
            return courses.get();
        }
    }
}
