package com.asos.reservationSystem.integrationTests;

import com.asos.reservationSystem.domain.dto.EmailChangeDto;
import com.asos.reservationSystem.domain.dto.NewPasswordDTO;
import com.asos.reservationSystem.domain.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserControllerIntegrationTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    @Autowired
    public UserControllerIntegrationTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void testThatGetUserSuccessfullyReturnsUser() throws Exception {
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
                        MockMvcRequestBuilders.get("/user")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.degree").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Ján"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Novák"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("student@student.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phoneNumber").value("+421949111111"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("STUDENT"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").doesNotExist());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/logout")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    public void testUploadPhotoSuccessfullyUploadsPhoto() throws Exception {
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

        byte[] imageContent = Files.readAllBytes(
                Path.of("src/test/java/com/asos/reservationSystem/unitTests/graf1.png"));

        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "test-photo.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                imageContent
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/photo")
                                .file(photo)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Image uploaded successfully."));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/logout")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    public void testGetPhotoSuccessfullyReturnsPhoto() throws Exception {
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

        Long userId = 2L;

        byte[] expectedPhotoBytes = Files.readAllBytes(
                Path.of("src/test/java/com/asos/reservationSystem/unitTests/graf1.png"));

        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "test-photo.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                expectedPhotoBytes
        );

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/photo")
                        .file(photo)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/photo/{id}", userId)
                                .header("Authorization", "Bearer " + token)
                                .accept(MediaType.IMAGE_JPEG)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(MockMvcResultMatchers.content().bytes(expectedPhotoBytes));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/logout")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    public void testGetPhotoReturnsNotFoundBecausePictureIsNull() throws Exception {
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

        Long userId = 3L;

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/photo/{id}", userId)
                                .header("Authorization", "Bearer " + token)
                                .accept(MediaType.IMAGE_JPEG)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG))
                .andExpect(MockMvcResultMatchers.content().string("Image not found for user."));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/logout")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    public void testGetPhotoReturnsNotFoundBecauseUserDoesNotExist() throws Exception {
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

        Long userId = 10L;

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/photo/{id}", userId)
                                .header("Authorization", "Bearer " + token)
                                .accept(MediaType.IMAGE_JPEG)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG))
                .andExpect(MockMvcResultMatchers.content().string("User does not exist."));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/logout")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    public void testDeletePhotoSuccessfullyRemovesPhoto() throws Exception {
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
                        MockMvcRequestBuilders.delete("/photo")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Profile photo removed successfully."));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/logout")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    public void testUpdateUserProfileSuccessfullyUpdatesProfile() throws Exception {
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

        UserDto userDto = new UserDto();
        userDto.setDegree("Bc.");
        userDto.setFirstName("Ján");
        userDto.setLastName("Novák");
        userDto.setPhoneNumber("+421949111111");
        userDto.setDescription("I am awesome");
        userDto.setEmail("student1@student.com");

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/updateUserProfile")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.degree").value("Bc."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Ján"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Novák"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("student@student.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phoneNumber").value("+421949111111"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("I am awesome"));
    }

    @Test
    public void testUpdateUserProfileUnsuccessfulBecauseOfWrongPhoneNumber() throws Exception {
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

        UserDto userDto = new UserDto();
        userDto.setDegree("Bc.");
        userDto.setFirstName("Ján");
        userDto.setLastName("Novák");
        userDto.setPhoneNumber("0949111111");
        userDto.setDescription("I am awesome");
        userDto.setEmail("student1@student.com");

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/updateUserProfile")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Wrong format of data."));
    }

    @Test
    public void testUpdateEmailSuccessfullyUpdatesEmail() throws Exception {
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

        EmailChangeDto emailChangeDto = new EmailChangeDto();
        emailChangeDto.setEmail("student@student.com");
        emailChangeDto.setNewEmail("newstudent@student.com");
        emailChangeDto.setPassword("student");

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/updateEmail")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(emailChangeDto))
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUpdateEmailUnsuccessfulBecauseOfNewEmail() throws Exception {
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

        EmailChangeDto emailChangeDto = new EmailChangeDto();
        emailChangeDto.setEmail("student@student.com");
        emailChangeDto.setNewEmail("student@student.com");
        emailChangeDto.setPassword("student");

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/updateEmail")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(emailChangeDto))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("New email is equal to actual email."));
    }

    @Test
    public void testUpdateEmailUnsuccessfulBecauseOfOldEmail() throws Exception {
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

        EmailChangeDto emailChangeDto = new EmailChangeDto();
        emailChangeDto.setEmail("student1@student.com");
        emailChangeDto.setNewEmail("newStudent@student.com");
        emailChangeDto.setPassword("student");

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/updateEmail")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(emailChangeDto))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Old email is not equal to actual email."));
    }

    @Test
    public void testUpdateEmailUnsuccessfulBecauseOfWrongPassword() throws Exception {
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

        EmailChangeDto emailChangeDto = new EmailChangeDto();
        emailChangeDto.setEmail("student@student.com");
        emailChangeDto.setNewEmail("newStudent@student.com");
        emailChangeDto.setPassword("student1");

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/updateEmail")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(emailChangeDto))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Password is incorrect."));
    }

    @Test
    public void testUpdatePasswordSuccessfullyUpdatesPassword() throws Exception {
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

        NewPasswordDTO newPasswordDto = new NewPasswordDTO();
        newPasswordDto.setPassword("student");
        newPasswordDto.setNewPassword("newPassword123");

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/updatePassword")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newPasswordDto))
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUpdatePasswordUnsuccessfulBecauseOfNewPassword() throws Exception {
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

        NewPasswordDTO newPasswordDto = new NewPasswordDTO();
        newPasswordDto.setPassword("student");
        newPasswordDto.setNewPassword("student");

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/updatePassword")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newPasswordDto))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("New password is equal to old password."));
    }

    @Test
    public void testUpdatePasswordUnsuccessfulBecauseOfWrongPassword() throws Exception {
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

        NewPasswordDTO newPasswordDto = new NewPasswordDTO();
        newPasswordDto.setPassword("student1");
        newPasswordDto.setNewPassword("studentNew");

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/updatePassword")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newPasswordDto))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Password is incorrect."));
    }

}
