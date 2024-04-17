package com.thbs.backend.Repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.thbs.backend.Models.ReviewDetails;


public interface ReviewRepo extends JpaRepository<ReviewDetails, UUID> {

    List<ReviewDetails> findByEventId(UUID eventId);

}

