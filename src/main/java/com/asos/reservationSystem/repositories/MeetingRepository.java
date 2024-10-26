package com.asos.reservationSystem.repositories;

import com.asos.reservationSystem.domain.entities.Meeting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends CrudRepository<Meeting, Long> {
}
