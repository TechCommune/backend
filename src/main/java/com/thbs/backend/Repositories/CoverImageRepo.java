package com.thbs.backend.Repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thbs.backend.Models.CoverImageModel;
import java.util.List;


public interface CoverImageRepo extends JpaRepository<CoverImageModel,UUID> {
    
    List<CoverImageModel> findByCoverImageId(UUID coverImageId);
    List<CoverImageModel> findByImageURLStartingWith(String imageURL);
    List<CoverImageModel> findByOrganizerId(UUID organizerId);
    List<CoverImageModel> findByEventId(UUID eventId);
}
