package com.thbs.backend.Repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thbs.backend.Models.EventProvider;

public interface EventProviderRepo extends JpaRepository <EventProvider,UUID>{
    EventProvider findByEmail(String email);
   
} 