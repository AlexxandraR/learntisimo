package com.asos.reservationSystem.unitTests;

import com.asos.reservationSystem.auth.AuthenticationService;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.exception.CustomException;
import com.asos.reservationSystem.repositories.UserRepository;
import com.asos.reservationSystem.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void saveUserPhotoShouldSavePhotoWhenUserExistsAndPhotoIsProvided() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);

        MultipartFile mockPhotoFile = Mockito.mock(MultipartFile.class);
        byte[] photoBytes = Files.readAllBytes(
                Paths.get("src/test/java/com/asos/reservationSystem/unitTests/graf1.png"));
        Mockito.when(mockPhotoFile.isEmpty()).thenReturn(false);
        Mockito.when(mockPhotoFile.getBytes()).thenReturn(photoBytes);

        Optional<User> optionalUser = Optional.of(mockUser);

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.saveUserPhoto(optionalUser, mockPhotoFile);

        assertNotNull(mockUser.getPhoto());
        assertEquals(photoBytes.length, mockUser.getPhoto().length());
        Mockito.verify(userRepository).save(mockUser);
    }

    @Test
    void saveUserPhotoShouldThrowExceptionWhenUserDoesNotExist() {
        Exception exception = assertThrows(CustomException.class, () ->
                userService.saveUserPhoto(Optional.empty(), null)
        );

        assertEquals("User does not exist.", exception.getMessage());
    }

    @Test
    void saveUserPhotoShouldNotSavePhotoWhenPhotoFileIsEmpty() {
        User mockUser = new User();
        Optional<User> optionalUser = Optional.of(mockUser);

        MultipartFile emptyPhotoFile = Mockito.mock(MultipartFile.class);
        Mockito.when(emptyPhotoFile.isEmpty()).thenReturn(true);

        userService.saveUserPhoto(optionalUser, emptyPhotoFile);

        assertNull(mockUser.getPhoto());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void getUserPhotoShouldReturnPhotoWhenUserExistsAndPhotoIsPresent() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);

        byte[] photoBytes = Files.readAllBytes(
                Paths.get("src/test/java/com/asos/reservationSystem/unitTests/graf1.png"));
        Blob photoBlob = new SerialBlob(photoBytes);
        mockUser.setPhoto(photoBlob);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        byte[] result = userService.getUserPhoto(1L);

        assertArrayEquals(photoBytes, result);
        Mockito.verify(userRepository).findById(1L);
    }

    @Test
    void getUserPhotoShouldThrowExceptionWhenUserDoesNotExist() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomException.class, () ->
                userService.getUserPhoto(1L)
        );

        assertEquals("User does not exist.", exception.getMessage());
    }

    @Test
    void getUserPhotoShouldThrowExceptionWhenPhotoIsNotFound() {
        User mockUser = new User();
        mockUser.setId(1L);

        mockUser.setPhoto(null);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        Exception exception = assertThrows(CustomException.class, () ->
                userService.getUserPhoto(1L)
        );

        assertEquals("Image not found for user.", exception.getMessage());
    }

    @Test
    void removeUserPhotoShouldRemovePhotoWhenUserExists() throws IOException, SQLException {
        User mockUser = new User();
        mockUser.setId(1L);

        Optional<User> optionalUser = Optional.of(mockUser);
        byte[] photoBytes = Files.readAllBytes(
                Paths.get("src/test/java/com/asos/reservationSystem/unitTests/graf1.png"));
        Blob photoBlob = new SerialBlob(photoBytes);
        mockUser.setPhoto(photoBlob);

        Mockito.when(userRepository.save(mockUser)).thenAnswer(invocation -> invocation.getArgument(0));

        userService.removeUserPhoto(optionalUser);

        assertNull(mockUser.getPhoto(), "The photo should be removed.");

        Mockito.verify(userRepository).save(mockUser);
    }

    @Test
    void removeUserPhotoShouldThrowExceptionWhenUserIsEmpty() {
        Optional<User> optionalUser = Optional.empty();

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.removeUserPhoto(optionalUser));

        assertEquals("User does not exist.", exception.getMessage(),
                "Exception message should be 'User does not exist.'");
    }

    @Test
    void removeUserPhotoShouldThrowExceptionWhenExceptionOccursDuringSave() {
        User mockUser = new User();
        mockUser.setId(1L);

        Optional<User> optionalUser = Optional.of(mockUser);
        Mockito.doThrow(new RuntimeException("Database error")).when(userRepository).save(mockUser);

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.removeUserPhoto(optionalUser));

        assertEquals("The image could not be removed.", exception.getMessage(),
                "Exception message should be 'The image could not be removed.'");
    }

    @Test
    void updateProfileShouldUpdateUserWhenUserExists() {
        User mockUser = new User();
        mockUser.setId(1L);

        Optional<User> optionalUser = Optional.of(mockUser);

        User userData = new User();
        userData.setDegree("Master");
        userData.setFirstName("John");
        userData.setLastName("Doe");
        userData.setPhoneNumber("123456789");
        userData.setDescription("Description");

        Mockito.when(userRepository.save(mockUser)).thenAnswer(invocation -> invocation.getArgument(0));

        userService.updateProfile(optionalUser, userData);

        assertEquals("Master", mockUser.getDegree());
        assertEquals("John", mockUser.getFirstName());
        assertEquals("Doe", mockUser.getLastName());
        assertEquals("123456789", mockUser.getPhoneNumber());
        assertEquals("Description", mockUser.getDescription());

        Mockito.verify(userRepository).save(mockUser);
    }

    @Test
    void updateProfileShouldThrowExceptionWhenUserIsEmpty() {
        Optional<User> optionalUser = Optional.empty();
        User userData = new User();

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.updateProfile(optionalUser, userData));

        assertEquals("User does not exist.", exception.getMessage());
    }

    @Test
    void updateEmailShouldUpdateEmailWhenValidInputs() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("old@example.com");
        mockUser.setPassword("hashedPassword");

        Optional<User> optionalUser = Optional.of(mockUser);

        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";
        String password = "validPassword";

        Mockito.when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(true);
        Mockito.when(userRepository.save(mockUser)).thenAnswer(invocation -> invocation.getArgument(0));

        userService.updateEmail(optionalUser, oldEmail, newEmail, password);

        assertEquals(newEmail, mockUser.getEmail());
        Mockito.verify(userRepository).save(mockUser);
        Mockito.verify(authenticationService).revokeAllUserTokens(mockUser);
    }

    @Test
    void updateEmailShouldThrowExceptionWhenUserIsEmpty() {
        Optional<User> optionalUser = Optional.empty();
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";
        String password = "validPassword";

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.updateEmail(optionalUser, oldEmail, newEmail, password));

        assertEquals("User does not exist.", exception.getMessage());
    }

    @Test
    void updateEmailShouldThrowExceptionWhenNewEmailIsSameAsOldEmail() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("old@example.com");
        Optional<User> optionalUser = Optional.of(mockUser);

        String oldEmail = "old@example.com";
        String newEmail = "old@example.com";
        String password = "validPassword";

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.updateEmail(optionalUser, oldEmail, newEmail, password));

        assertEquals("New email is equal to actual email.", exception.getMessage());
    }

    @Test
    void updateEmailShouldThrowExceptionWhenOldEmailDoesNotMatch() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("old@example.com");
        Optional<User> optionalUser = Optional.of(mockUser);

        String oldEmail = "incorrect@example.com";
        String newEmail = "new@example.com";
        String password = "validPassword";

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.updateEmail(optionalUser, oldEmail, newEmail, password));

        assertEquals("Old email is not equal to actual email.", exception.getMessage());
    }

    @Test
    void updateEmailShouldThrowExceptionWhenPasswordIsIncorrect() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("old@example.com");
        mockUser.setPassword("hashedPassword");
        Optional<User> optionalUser = Optional.of(mockUser);

        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";
        String password = "wrongPassword";

        Mockito.when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.updateEmail(optionalUser, oldEmail, newEmail, password));

        assertEquals("Password is incorrect.", exception.getMessage());
    }

    @Test
    void updatePasswordShouldUpdatePasswordWhenValidInputs() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setPassword("oldEncodedPassword");

        Optional<User> optionalUser = Optional.of(mockUser);

        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        Mockito.when(passwordEncoder.matches(oldPassword, mockUser.getPassword())).thenReturn(true);
        Mockito.when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        Mockito.when(userRepository.save(mockUser)).thenAnswer(invocation -> invocation.getArgument(0));

        userService.updatePassword(optionalUser, newPassword, oldPassword);

        assertEquals("encodedNewPassword", mockUser.getPassword());
        Mockito.verify(userRepository).save(mockUser);
        Mockito.verify(authenticationService).revokeAllUserTokens(mockUser);
    }

    @Test
    void updatePasswordShouldThrowExceptionWhenUserIsEmpty() {
        Optional<User> optionalUser = Optional.empty();
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.updatePassword(optionalUser, newPassword, oldPassword));

        assertEquals("User does not exist.", exception.getMessage());
    }

    @Test
    void updatePasswordShouldThrowExceptionWhenNewPasswordIsSameAsOldPassword() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setPassword("oldEncodedPassword");

        Optional<User> optionalUser = Optional.of(mockUser);

        String oldPassword = "oldPassword";
        String newPassword = "oldPassword";

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.updatePassword(optionalUser, newPassword, oldPassword));

        assertEquals("New password is equal to old password.", exception.getMessage());
    }

    @Test
    void updatePasswordShouldThrowExceptionWhenOldPasswordDoesNotMatch() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setPassword("oldEncodedPassword");

        Optional<User> optionalUser = Optional.of(mockUser);

        String oldPassword = "incorrectOldPassword"; // Incorrect old password
        String newPassword = "newPassword";

        Mockito.when(passwordEncoder.matches(oldPassword, mockUser.getPassword())).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.updatePassword(optionalUser, newPassword, oldPassword));

        assertEquals("Password is incorrect.", exception.getMessage());
    }
}