package com.thbs.backend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thbs.backend.Models.Event;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

import java.util.UUID;


@Repository
public interface EventRepo extends JpaRepository<Event, UUID> {

    Event findByTitle(String title);

    Event findByEventId(UUID eventId);


    


    @Transactional
    @Query(value = "SELECT * FROM event_db WHERE event_org_id =:event_org_id AND title =:title", nativeQuery = true)
    Event findByEventOrgIdAndEventTitle(@Param("event_org_id") UUID event_org_id, @Param("title") String title);

    @Transactional
    @Query(value = "SELECT * FROM event_db WHERE event_org_id =:event_org_id AND event_id =:event_id", nativeQuery = true)
    Event findByEventOrgIdAndEventId(@Param("event_org_id") UUID event_org_id, @Param("event_id") UUID event_id);

}
