package com.asos.reservationSystem.controllers;

import com.asos.reservationSystem.domain.dto.TeachingRequestDto;
import com.asos.reservationSystem.domain.entities.TeachingRequest;
import com.asos.reservationSystem.mappers.Mapper;
import com.asos.reservationSystem.services.TeachingRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TeachingRequestController {
    private final TeachingRequestService requestService;

    private final Mapper<TeachingRequest, TeachingRequestDto> requestMapper;

    private Logger logger;

    public TeachingRequestController(TeachingRequestService requestService, Mapper<TeachingRequest, TeachingRequestDto> requestMapper) {
        this.requestService = requestService;
        this.requestMapper = requestMapper;
        this.logger = LoggerFactory.getLogger(TeachingRequestController.class);
    }
}
