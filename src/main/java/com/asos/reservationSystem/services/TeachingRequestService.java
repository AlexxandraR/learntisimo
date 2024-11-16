package com.asos.reservationSystem.services;

import com.asos.reservationSystem.domain.entities.TeachingRequest;
import com.asos.reservationSystem.domain.entities.TeachingRequestStatus;
import com.asos.reservationSystem.domain.entities.User;

import java.util.List;
import java.util.Optional;

public interface TeachingRequestService {

    void applyTeacher(Optional<User> teacher);

    List<TeachingRequest> getTeachingRequests(Optional<User> connectedUser);

    void updateTeachingRequestStatus(Long requestId, User userDto, Optional<User> connectedUser,
                                     TeachingRequestStatus status);
}
