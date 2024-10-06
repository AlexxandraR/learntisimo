package com.asos.reservationSystem.domain;

import com.asos.reservationSystem.auth.AuthenticationService;
import com.asos.reservationSystem.config.JwtService;
import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.repositories.CourseRepository;
import com.asos.reservationSystem.repositories.MeetingRepository;
import com.asos.reservationSystem.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DBInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final MeetingRepository meetingRepository;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    public DBInitializer(UserRepository userRepository, CourseRepository courseRepository,
                         MeetingRepository meetingRepository, JwtService jwtService,
                         AuthenticationService authenticationService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.meetingRepository = meetingRepository;
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        var user = User.builder()
                .firstName("Admin")
                .lastName("1")
                .email("admin@admin.sk")
                .phoneNumber("+421949000000")
                .password(passwordEncoder.encode("admin"))
                .role(Role.ADMIN)
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshedToken = jwtService.generateRefreshToken(user);
        authenticationService.saveUserToken(savedUser, jwtToken, true);
        authenticationService.saveUserToken(savedUser, refreshedToken, false);

        var user1 = User.builder()
                .firstName("John")
                .lastName("Boe")
                .email("student@student.com")
                .phoneNumber("+421949000000")
                .password(passwordEncoder.encode("student"))
                .role(Role.STUDENT)
                .build();
        var savedUser1 = userRepository.save(user1);
        var jwtToken1 = jwtService.generateToken(user1);
        var refreshedToken1 = jwtService.generateRefreshToken(user1);
        authenticationService.saveUserToken(savedUser1, jwtToken1, true);
        authenticationService.saveUserToken(savedUser1, refreshedToken1, false);

        var user2 = User.builder()
                .firstName("Sanne")
                .lastName("Boe")
                .email("teacher@teacher.com")
                .phoneNumber("+421949000000")
                .password(passwordEncoder.encode("teacher"))
                .role(Role.TEACHER)
                .degree("Mgr.")
                .description("I am awesome!")
                .build();
        var savedUser2 = userRepository.save(user2);
        var jwtToken2 = jwtService.generateToken(user2);
        var refreshedToken2 = jwtService.generateRefreshToken(user2);
        authenticationService.saveUserToken(savedUser2, jwtToken2, true);
        authenticationService.saveUserToken(savedUser2, refreshedToken2, false);

        var course = Course.builder()
                .name("Fyzika")
                .price(10.50)
                .room("AB-300")
                .teacher(savedUser2)
                .students(Arrays.asList(savedUser1))
                .build();
        var savedCourse = courseRepository.save(course);

        var meeting = Meeting.builder()
                .beginning(LocalDateTime.now())
                .duration(1)
                .student(savedUser1)
                .teacher(savedUser2)
                .course(savedCourse)
                .build();
        var savedMeeting = meetingRepository.save(meeting);
    }
}
