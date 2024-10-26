package com.asos.reservationSystem.services.impl;

import com.asos.reservationSystem.repositories.MeetingRepository;
import com.asos.reservationSystem.services.MeetingService;
import org.springframework.stereotype.Service;

@Service
public class MeetingServiceImpl implements MeetingService {
    private MeetingRepository meetingRepository;

    public MeetingServiceImpl(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }
}
