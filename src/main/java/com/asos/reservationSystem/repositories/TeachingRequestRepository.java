package com.asos.reservationSystem.repositories;

import com.asos.reservationSystem.domain.entities.TeachingRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeachingRequestRepository extends CrudRepository<TeachingRequest, Long> {
}
