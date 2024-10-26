package com.asos.reservationSystem.mappers.impl;

import com.asos.reservationSystem.domain.dto.CourseDto;
import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CourseMapperImpl implements Mapper<Course, CourseDto> {
    private ModelMapper modelMapper;

    public CourseMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CourseDto mapToDto(Course course) {
        return modelMapper.map(course, CourseDto.class);
    }

    @Override
    public Course mapFromDto(CourseDto courseDto) {
        return modelMapper.map(courseDto, Course.class);
    }
}
