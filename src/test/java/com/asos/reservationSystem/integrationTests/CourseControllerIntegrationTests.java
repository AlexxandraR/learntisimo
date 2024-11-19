package com.asos.reservationSystem.integrationTests;

import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.repositories.CourseRepository;
import com.asos.reservationSystem.repositories.MeetingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CourseControllerIntegrationTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final CourseRepository courseRepository;
    private final MeetingRepository meetingRepository;

    @Autowired
    public CourseControllerIntegrationTests(MockMvc mockMvc, CourseRepository courseRepository,
                                            MeetingRepository meetingRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.courseRepository = courseRepository;
        this.meetingRepository = meetingRepository;
    }

    @Test
    public void testThatGetAllCoursesSucceeds() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "admin@admin.sk",
                "password", "admin"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.get("/course")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(7))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Fyzika 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].price").value(15.00))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].room").value("AB-101"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].teacher.email").value(
                        "teacher@teacher.com"));
    }

    @Test
    public void testThatAssignToCourseSucceeds() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "petra.kovacova@student.com",
                "password", "student"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.patch("/assignToCourse/7")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(7L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Informatika  2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.students[0].email").value(
                        "petra.kovacova@student.com"));

        List<Course> courses = courseRepository.findByStudentsId(3L);
        assertTrue(courses.stream().anyMatch(course -> course.getId() == 7L));
    }

    @Test
    public void testThatAssignToCourseFailsWhenCourseNotFound() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "student@student.com",
                "password", "student"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.patch("/assignToCourse/999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(
                        "Course does not exist."));
    }

    @Test
    public void testThatAssignToCourseFailsWhenUserIsNotAStudent() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "teacher@teacher.com",
                "password", "teacher"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.patch("/assignToCourse/7")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(
                        "Only student can assign to a course."));
    }

    @Test
    public void testThatAssignToCourseFailsWhenUserAlreadyAssigned() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "student@student.com",
                "password", "student"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.patch("/assignToCourse/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(
                        "User has already been assigned to this course."));
    }

    @Test
    public void testThatDeleteFromCourseSucceeds() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "student@student.com",
                "password", "student"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteFromCourse/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.students[?(@.id == 2)]").doesNotExist());

        List<Course> courses = courseRepository.findByStudentsId(2L);
        assertFalse(courses.stream().anyMatch(course -> course.getId() == 1L));

        List<Meeting> meetings = meetingRepository.findByStudentId(2L);
        assertFalse(meetings.stream().anyMatch(meeting -> meeting.getCourse().getId() == 1L));
    }

    @Test
    public void testThatDeleteFromCourseFailsWhenCourseNotFound() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "student@student.com",
                "password", "student"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteFromCourse/999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Course does not exist."));
    }

    @Test
    public void testThatDeleteFromCourseFailsWhenUserIsNotAStudent() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "teacher@teacher.com",
                "password", "teacher"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteFromCourse/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(
                        "Only student can be removed from a course."));
    }

    @Test
    public void testThatDeleteFromCourseFailsWhenUserNotAssignedToCourse() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "petra.kovacova@student.com",
                "password", "student"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.delete("/deleteFromCourse/2")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(
                        "User was not assigned to this course."));
    }

    @Test
    public void testThatGetTeacherCoursesSucceeds() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "teacher@teacher.com",
                "password", "teacher"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.get("/teacherCourses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString(
                        "\"id\":1")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString(
                        "\"id\":2")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString(
                        "\"id\":3")));
    }

    @Test
    public void testThatGetTeacherCoursesFailsWhenUserIsNotATeacher() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "student@student.com",
                "password", "student"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.get("/teacherCourses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Only teacher can use this endpoint: " +
                        "/teacherCourses."));
    }

    @Test
    public void testThatGetStudentCoursesSucceeds() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "student@student.com",
                "password", "student"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.get("/studentCourses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString(
                        "\"id\":1")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString(
                        "\"id\":2")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString(
                        "\"id\":5")));
    }

    @Test
    public void testThatGetStudentCoursesFailsWhenUserIsNotAStudent() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "teacher@teacher.com",
                "password", "teacher"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.get("/studentCourses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Only student can use this endpoint: " +
                        "/studentCourses."));
    }

    @Test
    public void testThatCreateCourseSucceeds() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "teacher@teacher.com",
                "password", "teacher"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        String newCourse = objectMapper.writeValueAsString(Map.of(
                "name", "ASOS",
                "price", 500.00,
                "room", "AB-404"
        ));

        mockMvc.perform(MockMvcRequestBuilders.post("/createCourse")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCourse))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("ASOS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(500.00))
                .andExpect(MockMvcResultMatchers.jsonPath("$.room").value("AB-404"));

        List<Course> courses = courseRepository.findByTeacherId(5L);
        assertTrue(courses.stream().anyMatch(course -> course.getId() == 8L));
    }

    @Test
    public void testThatSaveCourseFailsWhenUserIsNotATeacher() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "student@student.com",
                "password", "student"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        String newCourse = objectMapper.writeValueAsString(Map.of(
                "name", "New Course",
                "price", 50.00,
                "room", "XYZ-101"
        ));

        mockMvc.perform(MockMvcRequestBuilders.post("/createCourse")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCourse))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Only teacher can create course."));
    }

    @Test
    public void testThatRemoveCourseSucceeds() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "teacher@teacher.com",
                "password", "teacher"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.delete("/removeCourse/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        List<Course> courses = courseRepository.findByTeacherId(5L);
        assertFalse(courses.stream().anyMatch(course -> course.getId() == 1L));

        List<Meeting> meetings = meetingRepository.findByTeacherId(5L);
        assertFalse(meetings.stream().anyMatch(meeting -> meeting.getCourse().getId() == 1L));
    }

    @Test
    public void testThatRemoveCourseFailsWhenCourseNotFound() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "teacher@teacher.com",
                "password", "teacher"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.delete("/removeCourse/999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Course does not exist."));
    }

    @Test
    public void testThatRemoveCourseFailsWhenUserIsNotATeacher() throws Exception {
        String loginRequest = objectMapper.writeValueAsString(Map.of(
                "email", "student@student.com",
                "password", "student"
        ));

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("access_token")
                .asText();

        mockMvc.perform(MockMvcRequestBuilders.delete("/removeCourse/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Only teacher can delete course."));
    }
}
