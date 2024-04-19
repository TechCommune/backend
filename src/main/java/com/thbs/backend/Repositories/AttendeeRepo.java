package com.thbs.backend.Repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import com.thbs.backend.Models.AttendeeList;
import java.util.List;

@Repository

public interface AttendeeRepo extends JpaRepository<AttendeeList,UUID>{
    
    
    @Query
    // ("SELECT al.user_name, e.event_name, al.email " +
    //        "FROM attendee_list al " +
    //        "JOIN event_db e ON al.event_id = e.event_id " +
    //        "WHERE al.event_id = :eventId")
    // List<Object[]> findAttendeeDetailsByEventId(@Param("eventId") UUID eventId);

    List<AttendeeList> findByEventId(UUID eventId);
}
