package com.asos.reservationSystem.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailChangeDto {
    private String email;
    private String newEmail;
    private String password;
}
