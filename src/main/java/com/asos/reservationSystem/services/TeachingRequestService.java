package com.asos.reservationSystem.services;

import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.TeachingRequest;
import com.asos.reservationSystem.domain.entities.TeachingRequestStatus;
import com.asos.reservationSystem.domain.entities.User;

import java.util.List;

public interface TeachingRequestService {
    public void applyTeacher(User teacher);
    public List<TeachingRequest> getTeachingRequests(User connectedUser);

    void updateTeachingRequestStatus(Long teachingRequestId, TeachingRequestStatus status);
}
