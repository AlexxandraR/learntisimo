package com.asos.reservationSystem.integrationTests;

import com.asos.reservationSystem.domain.entities.Meeting;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MeetingControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MeetingRepository meetingRepository;

    @Test
    public void testThatGetTeacherMeetingsSucceeds() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.get("/teacherMeetings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(4))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers
                        .containsString("\"id\":1")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers
                        .containsString("\"id\":2")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers
                        .containsString("\"id\":6")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers
                        .containsString("\"id\":12")));
    }

    @Test
    public void testThatGetTeacherMeetingsFailsWhenUserIsNotATeacher() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.get("/teacherMeetings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Only teacher can use this endpoint: " +
                        "/teacherMeetings."));
    }

    @Test
    public void testThatGetStudentMeetingsSucceeds() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.get("/studentMeetings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers
                        .containsString("\"id\":1")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers
                        .containsString("\"id\":4")));
    }

    @Test
    public void testThatGetStudentMeetingsFailsWhenUserIsNotAStudent() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.get("/studentMeetings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Only student can use this endpoint: " +
                        "/studentMeetings."));
    }


    @Test
    public void testThatRemoveMeetingSucceeds() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.delete("/removeMeeting/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        List<Meeting> meetings = meetingRepository.findByTeacherId(5L);
        assertFalse(meetings.stream().anyMatch(meeting -> meeting.getId() == 1L));
    }

    @Test
    public void testThatRemoveMeetingFailsWhenMeetingNotFound() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.delete("/removeMeeting/999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Meeting does not exist."));
    }

    @Test
    public void testThatCreateMeetingSucceeds() throws Exception {
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

        String newMeeting = objectMapper.writeValueAsString(Map.of(
                "beginning", "2024-12-01T10:00:00",
                "duration", 60,
                "courseId", 1
        ));

        mockMvc.perform(MockMvcRequestBuilders.post("/createMeeting")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newMeeting))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(60))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beginning").value(
                        "2024-12-01T10:00:00"));

        List<Meeting> meetings = meetingRepository.findByTeacherId(5L);
        assertFalse(meetings.stream().anyMatch(meeting -> meeting.getId() == 13L));
    }

    @Test
    public void testThatCreateMeetingFailsWhenRoleNotTeacher() throws Exception {
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

        String newMeeting = objectMapper.writeValueAsString(Map.of(
                "beginning", "2024-12-01T10:00:00",
                "duration", 60,
                "courseId", 1
        ));

        mockMvc.perform(MockMvcRequestBuilders.post("/createMeeting")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newMeeting))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Only teacher can create meeting."));
    }

    @Test
    public void testThatCreateMeetingFailsWhenScheduledInPast() throws Exception {
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

        String newMeeting = objectMapper.writeValueAsString(Map.of(
                "beginning", "2023-01-01T10:00:00",
                "duration", 60,
                "courseId", 1
        ));

        mockMvc.perform(MockMvcRequestBuilders.post("/createMeeting")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newMeeting))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(
                        "Cannot schedule meeting in the past."));
    }

    @Test
    public void testThatRemoveStudentFromMeetingSucceeds() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.delete("/removeStudentMeeting/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Optional<Meeting> meetings = meetingRepository.findById(1L);
        assertNull(meetings.get().getStudent());
    }

    @Test
    public void testThatRemoveStudentFromMeetingFailsWhenUserNotAssignedToMeeting() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.delete("/removeStudentMeeting/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(
                        "User was not assigned to this meeting."));
    }

    @Test
    public void testThatGetCourseMeetingsFailsWhenUserIsNotAStudent() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.get("/courseMeetings/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(
                        "Only student can use this endpoint: /courseMeetings/{courseId}."));
    }

    @Test
    public void testThatGetCourseMeetingsReturnsEmptyWhenNoMeetingsAvailable() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.get("/courseMeetings/999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }


    @Test
    public void testThatAddStudentToMeetingSucceeds() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.patch("/addStudentToMeeting/6")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(6L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.student.email").value(
                        "student@student.com"));
        Optional<Meeting> meetings = meetingRepository.findById(6L);
        assertEquals(2L, (long) meetings.get().getStudent().getId());
    }

    @Test
    public void testThatAddStudentToMeetingFailsWhenUserIsNotAStudent() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.patch("/addStudentToMeeting/6")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Only student can assign to meeting."));
    }

}
