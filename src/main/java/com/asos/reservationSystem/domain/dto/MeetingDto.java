package com.asos.reservationSystem.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingDto {
    private Long id;
    private LocalDateTime beginning;
    private Integer duration;
    private CourseDto course;
    private UserDto teacher;
    private UserDto student;
}
