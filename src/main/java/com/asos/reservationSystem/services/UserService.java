package com.asos.reservationSystem.services;

import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Optional;

public interface UserService {
    Optional<User> getUser(Principal connectedCustomer);
    void saveUserPhoto(Optional<User> user, MultipartFile photoFile);
    byte[] getUserPhoto(Long id);
//    Optional<User> getUserByEmail(String email);
//    void denyTeacher(User user);

    void setRole(User user, Role role);

//    void acceptTeacher(User user);
    void updateProfile(Optional<User> user, User userData);
    void updateEmail(Optional<User> user, String oldEmail, String email, String password);
    void updatePassword(Optional<User> user, String newPassword, String oldPassword);
//    void checkRole(Principal connectedUser, Role role);
//    void checkConnectedToProvidedUser(Principal connectedUser, String id);
//    void checkConnectedToProvidedUserByEmail(Principal connectedUser, String email);

}
