package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.exception.CustomException;
import com.asos.reservationSystem.repositories.CourseRepository;
import com.asos.reservationSystem.repositories.MeetingRepository;
import com.asos.reservationSystem.services.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final MeetingRepository meetingRepository;

    public CourseServiceImpl(CourseRepository courseRepository, MeetingRepository meetingRepository) {
        this.courseRepository = courseRepository;
        this.meetingRepository = meetingRepository;
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
                    "Assignment to course: Course with id: " + courseId + " does not exist.", HttpStatus.NOT_FOUND);
        }
        if(user.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Assignment to course: User does not exist.", HttpStatus.NOT_FOUND);
        }
        if(user.get().getRole() != Role.STUDENT){
            throw new CustomException("Only student can assign to a course.",
                    "Assignment from course: Only student can assign to a course: " + courseId + ".",
                    HttpStatus.BAD_REQUEST);
        }
        if (course.get().getStudents().stream().noneMatch(student -> student.getId().equals(user.get().getId()))){
            course.get().getStudents().add(user.get());
            courseRepository.save(course.get());
        }
        else{
            throw new CustomException("User has already been assigned to this course.",
                    "Assignment to course: User has already been assigned to this course: " + courseId + ".",
                    HttpStatus.BAD_REQUEST);
        }
        return course;
    }

    @Override
    public Optional<Course> deleteFromCourse(Optional<User> user, Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if(course.isEmpty()){
            throw new CustomException("Course does not exist.",
                    "Removal from course: Course with id: " + courseId + " does not exist.", HttpStatus.NOT_FOUND);
        }
        if(user.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Removal from course: User does not exist.", HttpStatus.NOT_FOUND);
        }
        if(user.get().getRole() != Role.STUDENT){
            throw new CustomException("Only student can be removed from a course.",
                    "Removal from course: Only student can be removed from a course: " + courseId + ".",
                    HttpStatus.BAD_REQUEST);
        }
        if (course.get().getStudents().stream().noneMatch(student -> student.getId().equals(user.get().getId()))){
            throw new CustomException("User was not assigned to this course.",
                    "Removal from course: User was not assigned to this course: " + courseId + ".",
                    HttpStatus.BAD_REQUEST);
        }
        else{
            meetingRepository.setStudentToNullForCourseAndStudent(courseId, user.get().getId());
            meetingRepository.deletePastMeetingsWithNullStudent(courseId, LocalDateTime.now());
            course.get().getStudents().removeIf(student -> student.getId().equals(user.get().getId()));
            courseRepository.save(course.get());
        }
        return course;
    }

    public List<Course> getTeacherCourses(Optional<User> user) {
        if(user.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Get teacher courses: User does not exist.", HttpStatus.NOT_FOUND);
        }
        if(user.get().getRole() != Role.TEACHER){
            throw new CustomException("Only teacher can use this endpoint: /teacherCourses.",
                    "Get teacher's courses: Only teacher can use this endpoint: /teacherCourses.",
                    HttpStatus.BAD_REQUEST);
        }
        return new ArrayList<>(courseRepository.findByTeacherId(user.get().getId()));
    }

    public List<Course> getStudentCourses(Optional<User> user) {
        if(user.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Get student courses: User does not exist.", HttpStatus.NOT_FOUND);
        }
        if(user.get().getRole() != Role.STUDENT){
            throw new CustomException("Only student can use this endpoint: /studentCourses.",
                    "Get student's course: Only student can use this endpoint: /studentCourses.",
                    HttpStatus.BAD_REQUEST);
        }
        return new ArrayList<>(courseRepository.findByStudentsId(user.get().getId()));
    }

    @Override
    public Course saveCourse(Optional<User> user, Course course) {
        if (course == null) {
            throw new CustomException("Course is null.",
                    "Saving course: Course is null.", HttpStatus.BAD_REQUEST);
        }
        else if(user.isEmpty()){
            throw new CustomException("Teacher does not exist.",
                    "Saving course: Teacher does not exist.", HttpStatus.NOT_FOUND);
        }
        else if (user.get().getRole() != Role.TEACHER) {
            throw new CustomException("Only teacher can create course.",
                    "Saving course: Only teacher can create course.", HttpStatus.BAD_REQUEST);
        }
        course.setTeacher(user.get());
        return courseRepository.save(course);
    }

    @Override
    public void removeCourse(Long courseId, Optional<User> user) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            throw new CustomException("Course does not exist.",
                    "Removal of course: Course with id: " + courseId + " does not exist.", HttpStatus.NOT_FOUND);
        }
        else if(user.isEmpty()){
            throw new CustomException("Teacher does not exist.",
                    "Removal of course: Teacher does not exist.", HttpStatus.NOT_FOUND);
        }
        else if (user.get().getRole() != Role.TEACHER) {
            throw new CustomException("Only teacher can delete course.",
                    "Removal of course: Only teacher can delete course.", HttpStatus.BAD_REQUEST);
        }
        List<Meeting> meetings = meetingRepository.findByCourse(course.get());
        meetingRepository.deleteAll(meetings);
        courseRepository.deleteById(courseId);
    }
}
