package com.thbs.backend.Services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.thbs.backend.Models.ImagesDB;
import com.thbs.backend.Models.ResponseMessage;
import com.thbs.backend.Repositories.EventProviderRepo;
import com.thbs.backend.Repositories.ImagesDBRepo;


@Service
public class ImageUploadService {



    @Autowired
    private ImagesDBRepo imagesDBRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private EventProviderRepo eventProviderRepo;

    @Autowired
    private S3PutObjectService s3PutObjectService;

    @Autowired
    private ResponseMessage responseMessage;

    public ResponseEntity<ResponseMessage> uploadImageService(String token, String role, List<MultipartFile> images) {
        try {

            String failedImages = "";

            for (int i = 0; i < images.size(); i++) {
                
                String email = authService.verifyToken(token);
                UUID organizerId = eventProviderRepo.findByEmail(email).getId();

                // ResponseEntity<> organizerId = userService.getUserDetailsByEmailService(token, role);

                ImagesDB imagesDB = new ImagesDB();

                UUID imageUUID = UUID.randomUUID();
                UUID key=imageUUID;
                imagesDB.setImageId(imageUUID);

                imagesDB.setOrganizerId(organizerId);

                ResponseMessage messageFromPutObjectService = s3PutObjectService.putObjectService(organizerId.toString(), key.toString(),images.get(i)).getBody();

                if (messageFromPutObjectService.getSuccess()) {
                    imagesDB.setImageURL(messageFromPutObjectService.getMessage());
                    imagesDBRepo.save(imagesDB);
                } else {
                    failedImages += images.get(i).getOriginalFilename();
                    failedImages += ", ";
                    failedImages+= "Reason: "+messageFromPutObjectService.getMessage();
                }
            }

            if (failedImages.length() == 0) {
                responseMessage.setSuccess(true);
                responseMessage.setMessage("Upload successfully!");
                return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
            } else {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Upload failed for: " + failedImages);
                return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
            }

        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage("Internal Server Error inside ImageUploadService.java " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }

    }
}