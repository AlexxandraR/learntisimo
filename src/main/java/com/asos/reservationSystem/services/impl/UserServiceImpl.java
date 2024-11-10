package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.repositories.UserRepository;
import com.asos.reservationSystem.services.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getUser(Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return userRepository.findById(user.getId());
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public void acceptTeacher(User user) {
        userRepository.findUserByEmail(user.getEmail()).ifPresentOrElse(
                u -> {
                    u.setRole(Role.TEACHER);
                    userRepository.save(u);
                },
                () -> {
                    throw new RuntimeException("User not found");
                }
        );
    }

    @Override
    public void denyTeacher(User user) {
        userRepository.findUserByEmail(user.getEmail()).ifPresentOrElse(
                u -> {
                    u.setRole(Role.STUDENT);
                    userRepository.save(u);
                },
                () -> {
                    throw new RuntimeException("User not found");
                }
        );

    }
}
