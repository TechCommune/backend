package com.thbs.backend.Repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.thbs.backend.Models.Event;

import java.util.UUID;
import java.util.List;


public interface EventRepo extends JpaRepository <Event, UUID> {

    List<Event> findByEvent_org_id(UUID event_org_id);

    Event findByEventDetails(String title);




    
}