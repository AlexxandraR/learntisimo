package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.domain.entities.*;
import com.asos.reservationSystem.exception.CustomException;
import com.asos.reservationSystem.repositories.TeachingRequestRepository;
import com.asos.reservationSystem.services.CourseService;
import com.asos.reservationSystem.services.TeachingRequestService;
import com.asos.reservationSystem.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TeachingServiceImpl implements TeachingRequestService {
    private final TeachingRequestRepository teachingRequestRepository;
    private final UserService userService;
    private final CourseService courseService;


    public TeachingServiceImpl(TeachingRequestRepository teachingRequestRepository, UserService userService,
                               CourseService courseService) {
        this.teachingRequestRepository = teachingRequestRepository;
        this.userService = userService;
        this.courseService = courseService;
    }


    @Override
    public void applyTeacher(Optional<User> user) {
        if(user.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Teaching request: User does not exist.", HttpStatus.NOT_FOUND);
        }
        else if (user.get().getRole() != Role.STUDENT) {
            throw new CustomException("Only student can apply for teacher status.",
                    "Teaching request: Only student can apply for teacher status.", HttpStatus.BAD_REQUEST);
        }

        boolean requestExists = teachingRequestRepository.existsByTeacher(user.get());
        if (requestExists) {
            throw new CustomException("Teaching request already exists.",
                    "Teaching request: User has already applied for teacher status.", HttpStatus.CONFLICT);
        }

        var teachingRequest = new TeachingRequest();
        teachingRequest.setTeacher(user.get());
        teachingRequest.setStatus(TeachingRequestStatus.PENDING);
        teachingRequest.setDateTime(java.time.LocalDateTime.now());
        teachingRequestRepository.save(teachingRequest);
    }

    @Override
    public List<TeachingRequest> getTeachingRequests(Optional<User> user) {
        if(user.isEmpty()){
            throw new CustomException("User does not exist.",
                    "Get teaching request: User does not exist.", HttpStatus.NOT_FOUND);
        }
        else if (!user.get().getRole().equals(Role.ADMIN)) {
            throw new CustomException("Only admin can get teaching requests.",
                    "Get teaching request: Only admin can get teaching requests.", HttpStatus.BAD_REQUEST);
        }
        return StreamSupport.stream(teachingRequestRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public void updateTeachingRequestStatus(Long requestId, User user, Optional<User> admin,
                                            TeachingRequestStatus status) {
        if (admin.isEmpty()) {
            throw new CustomException("User does not exist.",
                    "Update teaching request status: User does not exist.", HttpStatus.NOT_FOUND);
        }
        if (!admin.get().getRole().equals(Role.ADMIN)) {
            throw new CustomException("Only admin can update teaching request status.",
                    "Update teaching request status: Only admin can update teaching request status.",
                    HttpStatus.BAD_REQUEST);
        }

        teachingRequestRepository.findById(requestId).ifPresentOrElse(
                teachingRequest -> {
                    if ((status == TeachingRequestStatus.APPROVED &&
                            teachingRequest.getStatus() == TeachingRequestStatus.APPROVED) ||
                            (status == TeachingRequestStatus.REJECTED
                                    && teachingRequest.getStatus() == TeachingRequestStatus.REJECTED)) {
                        throw new CustomException("Teaching request already " + status.toString().toLowerCase() + ".",
                                "Update teaching request status: Teaching request with id: " + requestId
                                        + " is already " + status.toString().toLowerCase() + ".",
                                HttpStatus.BAD_REQUEST);
                    }

                    teachingRequest.setStatus(status);
                    teachingRequestRepository.save(teachingRequest);

                    Optional<User> optionalUser = Optional.of(user);
                    if (status == TeachingRequestStatus.APPROVED) {
                        List<Course> courses = courseService.getStudentCourses(optionalUser);
                        courses.forEach(course -> courseService.deleteFromCourse(optionalUser, course.getId()));
                        userService.setRole(user, Role.TEACHER);
                    } else if (status == TeachingRequestStatus.REJECTED) {
                        List<Course> courses = courseService.getTeacherCourses(optionalUser);
                        courses.forEach(course -> courseService.removeCourse(course.getId(), optionalUser));
                        userService.setRole(user, Role.STUDENT);
                    }
                },
                () -> {
                    throw new CustomException("Teaching request not found.",
                            "Update teaching request status: Teaching request with id: " + requestId + " not found.",
                            HttpStatus.NOT_FOUND);
                }
        );
    }
}
