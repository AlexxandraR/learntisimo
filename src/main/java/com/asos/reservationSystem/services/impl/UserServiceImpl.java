package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.repositories.CourseRepository;
import com.asos.reservationSystem.repositories.UserRepository;
import com.asos.reservationSystem.services.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getUser(Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return userRepository.findById(user.getId());
    }
}
