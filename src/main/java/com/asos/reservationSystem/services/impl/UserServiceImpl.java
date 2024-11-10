package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.auth.AuthenticationService;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, AuthenticationService authenticationService, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.passwordEncoder = passwordEncoder;
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
    public void updateProfile(User user, Principal connectedUser) {
        getUser(connectedUser).ifPresentOrElse(
                u -> {
                    u.setDegree(user.getDegree());
                    u.setFirstName(user.getFirstName());
                    u.setLastName(user.getLastName());
                    u.setPhoneNumber(user.getPhoneNumber());
                    u.setDescription(user.getDescription());
                    userRepository.save(u);
                },
                () -> {
                    throw new RuntimeException("User not found");
                }
        );
    }

    @Override
    public void udpateEmail(Principal connectedUser, String email) {
        getUser(connectedUser).ifPresentOrElse(
                u -> {
                    u.setEmail(email);
                    userRepository.save(u);
                    authenticationService.revokeAllUserTokens(u);
                },
                () -> {
                    throw new RuntimeException("User not found");
                }
        );
    }

    @Override
    public void updatePassword(Principal connectedUser, String password) {
        getUser(connectedUser).ifPresentOrElse(
                u -> {
                    u.setPassword(passwordEncoder.encode(password));
                    userRepository.save(u);
                    authenticationService.revokeAllUserTokens(u);
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
