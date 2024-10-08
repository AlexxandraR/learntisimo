package com.asos.reservationSystem.mappers.impl;

import com.asos.reservationSystem.domain.dto.TeachingRequestDto;
import com.asos.reservationSystem.domain.entities.TeachingRequest;
import com.asos.reservationSystem.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TeachingRequestMapperImpl implements Mapper<TeachingRequest, TeachingRequestDto> {
    private ModelMapper modelMapper;

    public TeachingRequestMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public TeachingRequestDto mapToDto(TeachingRequest request) {
        return modelMapper.map(request, TeachingRequestDto.class);
    }

    @Override
    public TeachingRequest mapFromDto(TeachingRequestDto teachingRequestDto) {
        return modelMapper.map(teachingRequestDto, TeachingRequest.class);
    }
}
