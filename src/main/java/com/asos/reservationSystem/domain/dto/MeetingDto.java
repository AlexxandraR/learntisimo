package com.asos.reservationSystem.domain.dto;

import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
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
    private Course course;
    private User teacher;
    private User student;
}
