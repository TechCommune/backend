package com.thbs.backend.Services;

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
import com.thbs.backend.StaticData.S3Data;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class CoverImageUploadService {

    @Autowired
    private ImagesDBRepo imagesDBRepo;

    @Autowired
    private EventProviderRepo eventProviderRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private ResponseMessage responseMessage;

    public ResponseEntity<ResponseMessage> uploadCoverImageService(String EPToken, UUID eventId, MultipartFile image) {
        try {

            String email = authService.verifyToken(EPToken);
            UUID organizerId = eventProviderRepo.findByEmail(email).getId();

            String key = System.getenv("FOLDER_FOR_EVENTS")+"/" + eventId.toString() + "/" + image.getOriginalFilename();

            S3Client client = S3Data.s3Client;
            String bucketName = System.getenv("BUCKET_NAME");

            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(image.getContentType())
                    .build();

            RequestBody requestBody = RequestBody.fromInputStream(image.getInputStream(), image.getSize());

            PutObjectResponse response = client.putObject(putOb, requestBody);

            if (response.eTag().isEmpty()) {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Failed to upload cover image for event " + eventId);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
            } else {
                ImagesDB imagesDB = new ImagesDB();
                imagesDB.setImageId(UUID.randomUUID());
                imagesDB.setImageURL(key);
                imagesDB.setOrganizerId(organizerId);

                imagesDBRepo.save(imagesDB);

                responseMessage.setSuccess(true);
                responseMessage.setMessage("Cover image uploaded successfully for event " + eventId);
                return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
            }

        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage("Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    // public ResponseEntity<ResponseMessage> deleteCoverImageService(String EPToken, UUID eventId) {
    //     try {
            
    //         String email = authService.verifyToken(EPToken);
    //         UUID organizerId = eventProviderRepo.findByEmail(email).getId();
    //         // Check if the image exists in the database
    //         ImagesDB imageDB = imagesDBRepo.findByOrganizerIdAndEventId(organizerId, eventId);
    //         if (imageDB == null) {
    //             responseMessage.setSuccess(false);
    //             responseMessage.setMessage("Cover image not found for event " + eventId);
    //             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
    //         }

    //         // Delete the image from S3
    //         S3Client client = S3Data.s3Client;
    //         String bucketName = System.getenv("BUCKET_NAME");
    //         String key = System.getenv("FOLDER_FOR_EVENTS")+"/" + eventId.toString() + "/" + imageDB.getImageURL();

    //         client.deleteObject(builder -> builder.bucket(bucketName).key(key));

    //         // Delete the image record from the database
    //         imagesDBRepo.delete(imageDB);

    //         responseMessage.setSuccess(true);
    //         responseMessage.setMessage("Cover image deleted successfully for event " + eventId);
    //         return ResponseEntity.status(HttpStatus.OK).body(responseMessage);

    //     } catch (Exception e) {
    //         responseMessage.setSuccess(false);
    //         responseMessage.setMessage("Internal Server Error: " + e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
    //     }
    // }
}
