package com.asos.reservationSystem.mappers.impl;

import com.asos.reservationSystem.domain.dto.UserDto;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements Mapper<User, UserDto> {
    private ModelMapper modelMapper;

    public UserMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto mapToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public User mapFromDto(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}
