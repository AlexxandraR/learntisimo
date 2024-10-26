package com.asos.reservationSystem.mappers.impl;

import com.asos.reservationSystem.domain.dto.MeetingDto;
import com.asos.reservationSystem.domain.entities.Meeting;
import com.asos.reservationSystem.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MeetingMapperImpl implements Mapper<Meeting, MeetingDto> {
    private ModelMapper modelMapper;

    public MeetingMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public MeetingDto mapToDto(Meeting meeting) {
        return modelMapper.map(meeting, MeetingDto.class);
    }

    @Override
    public Meeting mapFromDto(MeetingDto meetingDto) {
        return modelMapper.map(meetingDto, Meeting.class);
    }
}
