package com.thbs.backend.Repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.thbs.backend.Models.EventEnrollment;
import java.util.List;


public interface EventEnrollRepo extends JpaRepository<EventEnrollment, UUID> {

    List<EventEnrollment> findByEventId(UUID eventId);
    List<EventEnrollment> findByUserId(UUID userId);
    long countByEventId(UUID eventId);
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EventEnrollment e WHERE e.userId = :userId AND e.eventId = :eventId")
    boolean existsByUserIdAndEventId(UUID userId, UUID eventId);

    void deleteByUserIdAndEventId(UUID userId, UUID eventId);

}
