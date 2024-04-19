package com.thbs.backend.Repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thbs.backend.Models.AttendeeList;

public interface AttendeeRepo extends JpaRepository<AttendeeList,UUID>{
    
    
}
