package com.thbs.backend.Repositories;

import java.util.UUID;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.thbs.backend.Models.ReviewDetails;

public interface ReviewRepo extends JpaRepository<ReviewDetails, UUID> {

    List<ReviewDetails> findByEventId(UUID eventId);

    @Query("SELECT r.eventId as eventId, AVG(r.rating) as avgRating, COUNT(r.rating) as ratingCount " +
           "FROM ReviewDetails r " +
           "WHERE r.eventId = :eventId " +
           "GROUP BY r.eventId")
    Map<String, Object> findEventRatingSummary(@Param("eventId") UUID eventId);
}
