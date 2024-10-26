package com.asos.reservationSystem.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
    private Long id;
    private String name;
    private Double price;
    private String room;
    private UserDto teacher;
    private List<UserDto> students;
}
