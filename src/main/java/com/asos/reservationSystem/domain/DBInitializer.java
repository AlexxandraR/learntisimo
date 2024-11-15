package com.asos.reservationSystem.domain;

import com.asos.reservationSystem.auth.AuthenticationService;
import com.asos.reservationSystem.config.JwtService;
import com.asos.reservationSystem.domain.entities.*;
import com.asos.reservationSystem.repositories.CourseRepository;
import com.asos.reservationSystem.repositories.MeetingRepository;
import com.asos.reservationSystem.repositories.TeachingRequestRepository;
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
    private final TeachingRequestRepository teachingRequestRepository;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    public DBInitializer(UserRepository userRepository, CourseRepository courseRepository,
                         MeetingRepository meetingRepository, TeachingRequestRepository teachingRequestRepository, JwtService jwtService,
                         AuthenticationService authenticationService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.meetingRepository = meetingRepository;
        this.teachingRequestRepository = teachingRequestRepository;
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

        var user11 = User.builder()
                .firstName("Pepek")
                .lastName("Namornik")
                .email("studen1t@student1.com")
                .phoneNumber("+421949000000")
                .password(passwordEncoder.encode("student"))
                .role(Role.STUDENT)
                .build();
        var savedUser11 = userRepository.save(user11);
        var jwtToken11 = jwtService.generateToken(user11);
        var refreshedToken11 = jwtService.generateRefreshToken(user11);
        authenticationService.saveUserToken(savedUser11, jwtToken11, true);
        authenticationService.saveUserToken(savedUser11, refreshedToken11, false);

        var user12 = User.builder()
                .firstName("Bulanec")
                .lastName("Bulancakovsky")
                .email("studen2t@student2.com")
                .phoneNumber("+421949000000")
                .password(passwordEncoder.encode("student"))
                .role(Role.STUDENT)
                .build();
        var savedUser12 = userRepository.save(user12);
        var jwtToken12 = jwtService.generateToken(user12);
        var refreshedToken12 = jwtService.generateRefreshToken(user12);
        authenticationService.saveUserToken(savedUser12, jwtToken12, true);
        authenticationService.saveUserToken(savedUser12, refreshedToken12, false);


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

        var user3 = User.builder()
                .firstName("Jakub")
                .lastName("Gombala")
                .email("kubinel47@gmail.com")
                .phoneNumber("+421949000001")
                .password(passwordEncoder.encode("Q1w2e3r4_"))
                .role(Role.TEACHER)
                .degree("Ing.")
                .description("TOP")
                .build();
        var savedUser3 = userRepository.save(user3);
        var jwtToken3 = jwtService.generateToken(user3);
        var refreshedToken3 = jwtService.generateRefreshToken(user3);
        authenticationService.saveUserToken(savedUser3, jwtToken3, true);
        authenticationService.saveUserToken(savedUser3, refreshedToken3, false);


        var course = Course.builder()
                .name("Fyzika")
                .price(10.50)
                .room("AB-300")
                .teacher(savedUser2)
                .students(Arrays.asList(savedUser1, savedUser11))
                .build();
        var savedCourse = courseRepository.save(course);

        var course2 = Course.builder()
                .name("Informatika")
                .price(25.0)
                .room("AB-150")
                .teacher(savedUser3)
                .students(Arrays.asList(savedUser1, savedUser12))
                .build();
        var savedCourse2 = courseRepository.save(course2);

        var course1 = Course.builder()
                .name("Matematika")
                .price(12.50)
                .room("BC-300")
                .teacher(savedUser2)
                .students(Arrays.asList(savedUser1, savedUser12))
                .build();
        var savedCourse1 = courseRepository.save(course1);

        var meeting = Meeting.builder()
                .beginning(LocalDateTime.now().minusDays(4))
                .duration(1)
                .student(savedUser1)
                .teacher(savedUser2)
                .course(savedCourse)
                .build();
        var savedMeeting = meetingRepository.save(meeting);

        var request = TeachingRequest.builder()
                .status(TeachingRequestStatus.APPROVED)
                .dateTime(LocalDateTime.now())
                .teacher(savedUser2)
                .build();
        var savedTeachingRequest = teachingRequestRepository.save(request);
    }
}
