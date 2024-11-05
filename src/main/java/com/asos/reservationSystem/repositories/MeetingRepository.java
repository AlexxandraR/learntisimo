package com.asos.reservationSystem.repositories;

import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.Meeting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends CrudRepository<Meeting, Long> {
    Optional<List<Meeting>> findAllByTeacher_Id(Long teacherId);
    Optional<List<Meeting>> findAllByStudent_Id(Long studentId);
    Optional<Meeting> findById(Long meetingId);
}
