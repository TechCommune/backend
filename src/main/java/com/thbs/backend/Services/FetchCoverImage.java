package com.thbs.backend.Services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.thbs.backend.Models.CoverImageModel;
import com.thbs.backend.Repositories.CoverImageRepo;

@Service
public class FetchCoverImage {


    
    @Autowired
    private CoverImageRepo coverImageRepo;
    
    
    public ResponseEntity<Object> fetchImagesService(UUID organizerId) {
        try {
            List <CoverImageModel> images = coverImageRepo.findByOrganizerId(organizerId);
            return ResponseEntity.ok(images);

            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

}