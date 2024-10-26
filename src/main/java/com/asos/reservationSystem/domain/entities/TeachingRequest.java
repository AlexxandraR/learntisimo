package com.asos.reservationSystem.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "teaching_requests")
public class TeachingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_id_seq")
    private Long id;

    @OneToOne
    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    @NotNull(message = "Teacher cannot be null")
    private User teacher;

    @NotNull(message = "Date and time cannot be null")
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status cannot be null")
    private TeachingRequestStatus status;
}
