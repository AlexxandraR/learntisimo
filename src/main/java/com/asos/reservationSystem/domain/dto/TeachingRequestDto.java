package com.asos.reservationSystem.domain.dto;

import com.asos.reservationSystem.domain.entities.TeachingRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeachingRequestDto {
    private Long id;
    private UserDto teacher;
    private LocalDateTime dateTime;
    private TeachingRequestStatus status;
}
