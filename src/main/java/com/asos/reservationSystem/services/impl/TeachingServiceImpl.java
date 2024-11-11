package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.domain.entities.Role;
import com.asos.reservationSystem.domain.entities.TeachingRequest;
import com.asos.reservationSystem.domain.entities.TeachingRequestStatus;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.exception.CustomException;
import com.asos.reservationSystem.repositories.TeachingRequestRepository;
import com.asos.reservationSystem.services.TeachingRequestService;
import org.springframework.http.HttpStatus;
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
        try {
            var teachingRequest = new TeachingRequest();
            teachingRequest.setTeacher(teacher);
            teachingRequest.setStatus(TeachingRequestStatus.PENDING);
            teachingRequest.setDateTime(java.time.LocalDateTime.now());
            teachingRequestRepository.save(teachingRequest);
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while applying teacher.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<TeachingRequest> getTeachingRequests(User connectedUser) {
        try {
            if (connectedUser.getRole().equals(Role.ADMIN)) {
                return StreamSupport.stream(teachingRequestRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while retrieving teaching requests.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateTeachingRequestStatus(Long teachingRequestId, TeachingRequestStatus status) {
        try {
            teachingRequestRepository.findById(teachingRequestId).ifPresentOrElse(
                    teachingRequest -> {
                        teachingRequest.setStatus(status);
                        teachingRequestRepository.save(teachingRequest);
                    },
                    () -> {
                        throw new CustomException("Teaching request not found.",
                                "Updating teaching request status: Teaching request with id: " + teachingRequestId + " not found.", HttpStatus.NOT_FOUND);
                    }
            );
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Unexpected error occurred while updating teaching request status.",
                    "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
