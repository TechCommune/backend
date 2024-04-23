package com.thbs.backend.Services;


import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thbs.backend.Models.ImagesDB;
import com.thbs.backend.Repositories.EventProviderRepo;
import com.thbs.backend.Repositories.ImagesDBRepo;


@Service
public class FetchImages {

    @Autowired
    private  AuthService authService;

    @Autowired
    private EventProviderRepo eventProviderRepo;


    @Autowired
    private ImagesDBRepo imagesDBRepo;



    public List<ImagesDB> fetchImagesService(String token, String role) {
        String email = authService.verifyToken(token);
        UUID organizerId = eventProviderRepo.findByEmail(email).getId();
        List<ImagesDB> images = imagesDBRepo.findByOrganizerId(organizerId);
        return images;
    }

   
}