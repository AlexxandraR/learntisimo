package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.MeetingDto;
import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.mappers.Mapper;
import com.asos.reservationSystem.services.MeetingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MeetingController {
    private final MeetingService meetingService;

    private final Mapper<Meeting, MeetingDto> meetingMapper;

    public MeetingController(MeetingService meetingService, Mapper<Meeting, MeetingDto> meetingMapper) {
        this.meetingService = meetingService;
        this.meetingMapper = meetingMapper;
    }
}
