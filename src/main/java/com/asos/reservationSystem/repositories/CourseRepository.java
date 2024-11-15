package com.asos.reservationSystem.repositories;

import com.asos.reservationSystem.domain.entities.Course;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {
    List<Course> findByTeacherId(Long teacherId);
    List<Course> findByStudentsId(Long studentId);
}
