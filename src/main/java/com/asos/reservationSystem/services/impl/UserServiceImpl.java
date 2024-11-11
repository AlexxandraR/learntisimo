package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.auth.AuthenticationService;
import com.asos.reservationSystem.exception.CustomException;
import org.springframework.http.HttpStatus;
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
        try {
            userRepository.findUserByEmail(user.getEmail()).ifPresentOrElse(
                    u -> {
                        u.setRole(Role.TEACHER);
                        userRepository.save(u);
                    },
                    () -> {
                        throw new CustomException("User not found.",
                                "Accepting teacher: User with email: " + user.getEmail() + " not found.", HttpStatus.NOT_FOUND);
                    }
            );
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while accepting teacher.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateProfile(User user, Principal connectedUser) {
        try {
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
                        throw new CustomException("User not found.",
                                "Updating profile: User not found.", HttpStatus.NOT_FOUND);
                    }
            );
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while updating profile.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateEmail(Principal connectedUser, String email, String password) {
        try {
            getUser(connectedUser).ifPresentOrElse(
                    u -> {
                        if (passwordEncoder.matches(password, u.getPassword())) {
                            u.setEmail(email);
                            userRepository.save(u);
                            authenticationService.revokeAllUserTokens(u);
                        } else {
                            throw new CustomException("Password is incorrect",
                                    "Updating email: Password is incorrect for user with id: " + u.getId(), HttpStatus.BAD_REQUEST);
                        }
                    },
                    () -> {
                        throw new CustomException("User " + email + " not found",
                                "Updating email: User not found.", HttpStatus.NOT_FOUND);
                    }
            );
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while updating email.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updatePassword(Principal connectedUser, String newPassword, String oldPassword) {
        try {
            getUser(connectedUser).ifPresentOrElse(
                    u -> {
                        if (passwordEncoder.matches(oldPassword, u.getPassword())) {
                            u.setPassword(passwordEncoder.encode(newPassword));
                            userRepository.save(u);
                            authenticationService.revokeAllUserTokens(u);
                        } else {
                            throw new CustomException("Password is incorrect",
                                    "Updating password: Password is incorrect for user with id: " + u.getId(), HttpStatus.BAD_REQUEST);
                        }
                    },
                    () -> {
                        throw new CustomException("User not found.",
                                "Updating password: User " +  connectedUser.getName() + " not found.", HttpStatus.NOT_FOUND);
                    }
            );
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while updating password.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void checkRole(Principal connectedUser, Role role) {
        try {
            getUser(connectedUser).ifPresentOrElse(
                    u -> {
                        if (u.getRole() != role) {
                            throw new CustomException("User does not have required role.",
                                    "Checking role: User does not have required role.", HttpStatus.FORBIDDEN);
                        }
                    },
                    () -> {
                        throw new CustomException("User not found.",
                                "Checking role: User "+ connectedUser.getName() +" not found.", HttpStatus.NOT_FOUND);
                    }
            );
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while checking role.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void checkConnectedToProvidedUser(Principal connectedUser, String id) {
        try {
            getUser(connectedUser).ifPresentOrElse(
                    u -> {
                        if (!u.getId().equals(Long.parseLong(id))) {
                            throw new CustomException("User is not connected to provided user.",
                                    "Checking connection: User is not connected to provided user with id: " + id, HttpStatus.FORBIDDEN);
                        }
                    },
                    () -> {
                        throw new CustomException("User not found.",
                                "Checking connection: User " + connectedUser.getName() + " not found.", HttpStatus.NOT_FOUND);
                    }
            );
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while checking connection.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void checkConnectedToProvidedUserByEmail(Principal connectedUser, String email) {
        try {
            getUser(connectedUser).ifPresentOrElse(
                    u -> {
                        if (!u.getEmail().equals(email)) {
                            throw new CustomException("User is not connected to provided user.",
                                    "Checking connection by email: User is not connected to provided user with email: " + email, HttpStatus.FORBIDDEN);
                        }
                    },
                    () -> {
                        throw new CustomException("User not found.",
                                "Checking connection by email: User " + connectedUser.getName() + " not found.", HttpStatus.NOT_FOUND);
                    }
            );
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while checking connection by email.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public void denyTeacher(User user) {
        try {
            userRepository.findUserByEmail(user.getEmail()).ifPresentOrElse(
                    u -> {
                        u.setRole(Role.STUDENT);
                        userRepository.save(u);
                    },
                    () -> {
                        throw new CustomException("User not found.",
                                "Denying teacher: User with email: " + user.getEmail() + " not found.", HttpStatus.NOT_FOUND);
                    }
            );
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while denying teacher.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
