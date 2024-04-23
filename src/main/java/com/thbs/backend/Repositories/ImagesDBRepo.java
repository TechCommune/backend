package com.thbs.backend.Repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thbs.backend.Models.ImagesDB;

public interface ImagesDBRepo extends JpaRepository<ImagesDB, UUID> {

    List<ImagesDB> findByOrganizerId(UUID organizerId);

    // @Query("SELECT i FROM ImagesDB i JOIN Event e ON i.eventId = e.eventId WHERE i.organizerId = :organizerId AND e.eventId = :eventId")
    // ImagesDB findByOrganizerIdAndEventId(@Param("organizerId") UUID organizerId, @Param("eventId") UUID eventId);

}
