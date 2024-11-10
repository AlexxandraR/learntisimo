package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.TeachingRequest;
import com.asos.reservationSystem.domain.entities.TeachingRequestStatus;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.repositories.TeachingRequestRepository;
import com.asos.reservationSystem.services.TeachingRequestService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TeachingServiceImpl implements TeachingRequestService {
    private final TeachingRequestRepository teachingRequestRepository;

    public TeachingServiceImpl(TeachingRequestRepository teachingRequestRepository) {
        this.teachingRequestRepository = teachingRequestRepository;
    }


    @Override
    public void applyTeacher(User teacher) {
        var teachingRequest = new TeachingRequest();
        teachingRequest.setTeacher(teacher);
        teachingRequest.setStatus(TeachingRequestStatus.PENDING);
        teachingRequest.setDateTime(java.time.LocalDateTime.now());
        teachingRequestRepository.save(teachingRequest);
    }

    @Override
    public List<TeachingRequest> getTeachingRequests(User connectedUser) {
        if (connectedUser.getRole().equals(Role.ADMIN)) {
            return StreamSupport.stream(teachingRequestRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void updateTeachingRequestStatus(Long teachingRequestId, TeachingRequestStatus status) {
        teachingRequestRepository.findById(teachingRequestId).ifPresentOrElse(
                teachingRequest -> {
                    teachingRequest.setStatus(status);
                    teachingRequestRepository.save(teachingRequest);
                },
                () -> {
                    throw new RuntimeException("Teaching request not found");
                }
        );
    }
}
