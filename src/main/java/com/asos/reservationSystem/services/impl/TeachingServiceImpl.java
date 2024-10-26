package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.repositories.TeachingRequestRepository;
import com.asos.reservationSystem.services.TeachingRequestService;
import org.springframework.stereotype.Service;

@Service
public class TeachingServiceImpl implements TeachingRequestService {
    private TeachingRequestRepository teachingRequestRepository;

    public TeachingServiceImpl(TeachingRequestRepository teachingRequestRepository) {
        this.teachingRequestRepository = teachingRequestRepository;
    }
}
