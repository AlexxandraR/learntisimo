package com.asos.reservationSystem.repositories;

import com.asos.reservationSystem.domain.entities.Course;
import com.asos.reservationSystem.domain.entities.Meeting;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends CrudRepository<Meeting, Long> {
    List<Meeting> findByCourse(Course course);
    List<Meeting> findByTeacherId(Long teacherId);
    List<Meeting> findByStudentId(Long studentId);

    Optional<Meeting> findById(Long meetingId);

    Optional<List<Meeting>> findAllByCourse_IdAndStudentIsNull(Long courseId);

    @Query(value = "SELECT * FROM meetings WHERE teacher_id = ?1 AND " +
            "(beginning < ?3 AND (beginning + duration * INTERVAL '1 minute') > ?2)", nativeQuery = true)
    List<Meeting> findByTeacherAndTimeRange(Long teacherId,
                                            LocalDateTime start,
                                            LocalDateTime end);

    @Query(value = "SELECT * FROM meetings WHERE student_id = ?1 AND " +
            "(beginning < ?3 AND (beginning + duration * INTERVAL '1 minute') > ?2)", nativeQuery = true)
    List<Meeting> findByStudentAndTimeRange(Long studentId,
                                            LocalDateTime start,
                                            LocalDateTime end);

    @Modifying
    @Transactional
    @Query("UPDATE Meeting m SET m.student = NULL WHERE m.course.id = ?1 AND m.student.id = ?2")
    void setStudentToNullForCourseAndStudent(Long courseId, Long studentId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Meeting m WHERE m.course.id = ?1 AND m.student IS NULL AND m.beginning < ?2")
    void deletePastMeetingsWithNullStudent(Long courseId, LocalDateTime currentDateTime);

}
