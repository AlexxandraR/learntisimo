package com.asos.reservationSystem.integrationTests;

import com.asos.reservationSystem.domain.dto.UserDto;
import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.repositories.CourseRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TeachingRequestIntegrationTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private final CourseRepository courseRepository;

    @Autowired
    public TeachingRequestIntegrationTests(MockMvc mockMvc, ObjectMapper objectMapper,
                                           CourseRepository courseRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.courseRepository = courseRepository;
    }

    @Test
    public void testApplyTeacherSuccessfullySubmitsRequest() throws Exception {
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

        String responseContent = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseContent).get("access_token").asText();

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/applyTeacher")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testApplyTeacherUnsuccessfulBecauseOfTeacherRole() throws Exception {
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

        String responseContent = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseContent).get("access_token").asText();

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/applyTeacher")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(
                        "Only student can apply for teacher status."));
    }

    @Test
    public void testApplyTeacherUnsuccessfulBecauseOfRepeatedApply() throws Exception {
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

        String responseContent = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseContent).get("access_token").asText();

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/applyTeacher")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/applyTeacher")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("Teaching request already exists."));
    }

    @Test
    public void testGetTeachingRequestsSuccessfullyReturnsRequests() throws Exception {
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

        String responseContent = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseContent).get("access_token").asText();

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/getTeachingRequests")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("PENDING"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].status").value("APPROVED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].status").value("APPROVED"));
    }

    @Test
    public void testGetTeachingRequestsUnsuccessfulBecauseOfNoAdminRole() throws Exception {
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

        String responseContent = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseContent).get("access_token").asText();

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/getTeachingRequests")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(
                        "Only admin can get teaching requests."));
    }

    @Test
    public void testAcceptRequestSuccessfullyUpdatesRequestStatus() throws Exception {
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

        String responseContent = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseContent).get("access_token").asText();

        UserDto userDto = new UserDto(2L, null, "Ján", "Novák",
                "student@student.com", "+421949111111", Role.STUDENT, null);
        String userDtoJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/acceptRequest/{requestId}", 1L)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());

        List<Course> courses = courseRepository.findByStudentsId(2L);
        assertTrue(courses.isEmpty());
    }

    @Test
    public void testAcceptRequestUnsuccessfulBecauseOfNoAdminRole() throws Exception {
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

        String responseContent = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseContent).get("access_token").asText();

        UserDto userDto = new UserDto(2L, null, "Ján", "Novák",
                "student@student.com", "+421949111111", Role.STUDENT, null);
        String userDtoJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/acceptRequest/{requestId}", 1L)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(
                        "Only admin can update teaching request status."));

        List<Course> courses = courseRepository.findByStudentsId(2L);
        assertFalse(courses.isEmpty());
    }

    @Test
    public void testAcceptRequestUnsuccessfulBecauseOfRepeatedApply() throws Exception {
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

        String responseContent = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseContent).get("access_token").asText();

        UserDto userDto = new UserDto(2L, null, "Ján", "Novák",
                "student@student.com", "+421949111111", Role.STUDENT, null);
        String userDtoJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/acceptRequest/{requestId}", 1L)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/acceptRequest/{requestId}", 1L)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(
                        "Teaching request already approved."));

        List<Course> courses = courseRepository.findByStudentsId(2L);
        assertTrue(courses.isEmpty());
    }

    @Test
    public void testRejectRequestSuccessfullyUpdatesRequestStatus() throws Exception {
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

        String responseContent = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseContent).get("access_token").asText();

        UserDto userDto = new UserDto(6L,  "PhDr.", "Tomáš", "Král",
                "tomas.kral@teacher.com", "+421949555555", Role.TEACHER,
                "Špecialista na informatiku a programovanie.");
        String userDtoJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/denyRequest/{requestId}", 3L)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());

        List<Course> courses = courseRepository.findByTeacherId(6L);
        assertTrue(courses.isEmpty());
    }

    @Test
    public void testRejectRequestUnsuccessfulBecauseOfNoAdminRole() throws Exception {
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

        String responseContent = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseContent).get("access_token").asText();

        UserDto userDto = new UserDto(6L,  "PhDr.", "Tomáš", "Král",
                "tomas.kral@teacher.com", "+421949555555", Role.TEACHER,
                "Špecialista na informatiku a programovanie.");
        String userDtoJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/denyRequest/{requestId}", 3L)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(
                        "Only admin can update teaching request status."));

        List<Course> courses = courseRepository.findByTeacherId(6L);
        assertFalse(courses.isEmpty());
    }

    @Test
    public void testRejectRequestUnsuccessfulBecauseOfRepeatedApply() throws Exception {
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

        String responseContent = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseContent).get("access_token").asText();

        UserDto userDto = new UserDto(6L,  "PhDr.", "Tomáš", "Král",
                "tomas.kral@teacher.com", "+421949555555", Role.TEACHER,
                "Špecialista na informatiku a programovanie.");
        String userDtoJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/denyRequest/{requestId}", 3L)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/denyRequest/{requestId}", 3L)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(
                        "Teaching request already rejected."));

        List<Course> courses = courseRepository.findByStudentsId(6L);
        assertTrue(courses.isEmpty());
    }

}
