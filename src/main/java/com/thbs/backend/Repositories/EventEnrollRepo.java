package com.thbs.backend.Repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thbs.backend.Models.EventEnrollment;
import java.util.List;


public interface EventEnrollRepo extends JpaRepository<EventEnrollment, UUID> {

    List<EventEnrollment> findByEventId(UUID eventId);
    List<EventEnrollment> findByUserId(UUID userId);
    long countByEventId(UUID eventId);
    
}
