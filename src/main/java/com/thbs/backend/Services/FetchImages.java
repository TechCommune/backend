package com.thbs.backend.Services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.thbs.backend.Models.AdminModel;
import com.thbs.backend.Models.ImagesDB;
import com.thbs.backend.Repositories.AdminRepo;
import com.thbs.backend.Repositories.ImagesDBRepo;

@Service
public class FetchImages {

    @Autowired
    private AuthService authService;

    @Autowired
    private AdminRepo adminRepo;
    @Autowired
    private ImagesDBRepo imagesDBRepo;

    public ResponseEntity<Object> fetchImagesService(String adminToken, UUID organizerId) {
        try {
            String email = authService.verifyToken(adminToken);
            AdminModel admin = adminRepo.findByEmail(email);
            if (admin != null) {
                List<ImagesDB> images = imagesDBRepo.findByOrganizerId(organizerId);
                return ResponseEntity.ok(images);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Only Admin can fetch the Documents of Event Organizer");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

}