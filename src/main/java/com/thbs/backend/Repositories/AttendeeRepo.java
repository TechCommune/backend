package com.thbs.backend.Repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.thbs.backend.Models.AttendeeList;
import java.util.List;

@Repository

public interface AttendeeRepo extends JpaRepository<AttendeeList, UUID> {

    List<AttendeeList> findByEventId(UUID eventId);
}
