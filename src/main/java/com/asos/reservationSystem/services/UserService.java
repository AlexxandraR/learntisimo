package com.asos.reservationSystem.services;

import com.asos.reservationSystem.domain.entities.User;

import java.security.Principal;
import java.util.Optional;

public interface UserService {
    Optional<User> getUser(Principal connectedCustomer);
}
