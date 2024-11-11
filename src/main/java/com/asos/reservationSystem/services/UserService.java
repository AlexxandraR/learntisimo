package com.asos.reservationSystem.services;

import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.User;

import java.security.Principal;
import java.util.Optional;

public interface UserService {
    Optional<User> getUser(Principal connectedCustomer);
    Optional<User> getUserByEmail(String email);
    void denyTeacher(User user);
    void acceptTeacher(User user);
    void updateProfile(User user, Principal connectedUser);
    void updateEmail(Principal connectedUser, String email, String password);
    void updatePassword(Principal connectedUser, String newPassword, String oldPassword);
    void checkRole(Principal connectedUser, Role role);
    void checkConnectedToProvidedUser(Principal connectedUser, String id);
    void checkConnectedToProvidedUserByEmail(Principal connectedUser, String email);

}
