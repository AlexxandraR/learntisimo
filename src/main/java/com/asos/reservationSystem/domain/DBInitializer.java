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
import java.util.List;

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
                         MeetingRepository meetingRepository, TeachingRequestRepository teachingRequestRepository,
                         JwtService jwtService, AuthenticationService authenticationService,
                         PasswordEncoder passwordEncoder) {
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
        var admin = createUser("Admin", "1", "admin@admin.sk", "+421949000000", "admin", Role.ADMIN, null, null);
        var student1 = createUser("Ján", "Novák", "student@student.com", "+421949111111", "student", Role.STUDENT, null, null);
        var student2 = createUser("Petra", "Kováčová", "petra.kovacova@student.com", "+421949222222", "student", Role.STUDENT, null, null);
        var student3 = createUser("Lukáš", "Horák", "lukas.horak@student.com", "+421949333333", "student", Role.STUDENT, null, null);

        var teacher1 = createUser("Mária", "Kučerová", "teacher@teacher.com", "+421949444444", "teacher", Role.TEACHER, "Mgr.", "Skúsená učiteľka fyziky a matematiky.");
        var teacher2 = createUser("Tomáš", "Král", "tomas.kral@teacher.com", "+421949555555", "teacher", Role.TEACHER, "PhDr.", "Špecialista na informatiku a programovanie.");

        var course1 = createCourse("Fyzika 1", 15.00, "AB-101", teacher1, List.of(student1, student2));
        var course2 = createCourse("Matematika 1", 20.00, "AB-102", teacher1, List.of(student1, student3));
        var course5 = createCourse("Matematika 2", 20.00, "AB-102", teacher1, List.of());
        var course3 = createCourse("Informatika 1", 25.00, "BC-201", teacher2, List.of(student2, student3));
        var course4 = createCourse("Programovanie v Jave 1", 30.00, "BC-202", teacher2, List.of(student1));
        var course6 = createCourse("Programovanie v Jave 2", 30.00, "BC-202", teacher2, List.of());
        var course7 = createCourse("Informatika  2", 35.00, "BC-202", teacher2, List.of());

        createMeeting(LocalDateTime.now().minusDays(2), 2, student1, teacher1, course1);
        createMeeting(LocalDateTime.now().minusDays(5), 1, student2, teacher1, course2);
        createMeeting(LocalDateTime.now().minusDays(1), 3, student3, teacher2, course3);
        createMeeting(LocalDateTime.now().minusHours(6), 2, student1, teacher2, course4);
        createMeeting(LocalDateTime.now().plusDays(6), 60, null, teacher2, course4);
        createMeeting(LocalDateTime.now().plusDays(6), 60, null, teacher1, course2);
        createMeeting(LocalDateTime.now().plusDays(8), 60, null, teacher2, course6);
        createMeeting(LocalDateTime.now().plusDays(9), 60, null, teacher2, course6);
        createMeeting(LocalDateTime.now().plusDays(10), 60, null, teacher2, course6);
        createMeeting(LocalDateTime.now().plusDays(11), 60, null, teacher2, course6);
        createMeeting(LocalDateTime.now().plusDays(1), 60, null, teacher2, course7);
        createMeeting(LocalDateTime.now().plusDays(2), 60, null, teacher1, course5);

        createTeachingRequest(LocalDateTime.now().minusDays(3), teacher1, TeachingRequestStatus.APPROVED);
        createTeachingRequest(LocalDateTime.now().minusDays(7), teacher2, TeachingRequestStatus.APPROVED);
    }

    private User createUser(String firstName, String lastName, String email, String phoneNumber, String password, Role role, String degree, String description) {
        var user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phoneNumber(phoneNumber)
                .password(passwordEncoder.encode(password))
                .role(role)
                .degree(degree)
                .description(description)
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshedToken = jwtService.generateRefreshToken(user);
        authenticationService.saveUserToken(savedUser, jwtToken, true);
        authenticationService.saveUserToken(savedUser, refreshedToken, false);
        return savedUser;
    }

    private Course createCourse(String name, double price, String room, User teacher, List<User> students) {
        var course = Course.builder()
                .name(name)
                .price(price)
                .room(room)
                .teacher(teacher)
                .students(students)
                .build();
        return courseRepository.save(course);
    }

    private void createMeeting(LocalDateTime beginning, int duration, User student, User teacher, Course course) {
        var meeting = Meeting.builder()
                .beginning(beginning)
                .duration(duration)
                .student(student)
                .teacher(teacher)
                .course(course)
                .build();
        meetingRepository.save(meeting);
    }

    private void createTeachingRequest(LocalDateTime dateTime, User teacher, TeachingRequestStatus status) {
        var request = TeachingRequest.builder()
                .status(status)
                .dateTime(dateTime)
                .teacher(teacher)
                .build();
        teachingRequestRepository.save(request);
    }
}
