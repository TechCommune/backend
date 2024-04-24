package com.thbs.backend.Services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.thbs.backend.Models.CoverImageModel;
import com.thbs.backend.Models.ResponseMessage;
import com.thbs.backend.Repositories.CoverImageRepo;
import com.thbs.backend.Repositories.EventProviderRepo;
import com.thbs.backend.StaticData.S3Data;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class CoverImageUploadService {

    @Autowired
    private CoverImageRepo coverImageRepo;


    @Autowired
    private EventProviderRepo eventProviderRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private ResponseMessage responseMessage;

    @Autowired
    private S3PutObjectService s3PutObjectService;



    public ResponseEntity<ResponseMessage> uploadCoverImageService(String EPToken, UUID eventId, MultipartFile image) {
    try {
        String email = authService.verifyToken(EPToken);
        UUID organizerId = eventProviderRepo.findByEmail(email).getId();

        //check whether there are any cover images associated to an event in cover_image_model db

        String key_to_check_image_exists_for_an_event = System.getenv("FOLDER_FOR_EVENTS") + "/" + eventId.toString() + "/";
        
        S3Client client = S3Data.s3Client;
        String bucketName = System.getenv("BUCKET_NAME");
        
        List <CoverImageModel> cover_images = coverImageRepo.findByEventId(eventId);
        
        if(cover_images!=null)
        {
            for(CoverImageModel cover_image : cover_images )
            {
                
                // Delete the image from S3 bucket
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key_to_check_image_exists_for_an_event + cover_image.getCoverImageId())
                        .build();
                client.deleteObject(deleteObjectRequest);
        
                // Delete the corresponding entry from the ImagesDB table
                coverImageRepo.deleteById(cover_image.getCoverImageId());
            }
        }

        UUID coverImageId = UUID.randomUUID();

        String key = key_to_check_image_exists_for_an_event + coverImageId;

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
            CoverImageModel coverImageModel = new CoverImageModel();
            coverImageModel.setCoverImageId(coverImageId);
            coverImageModel.setImageURL(s3PutObjectService.preSignedURLService(organizerId.toString(), key).getBody().getMessage());
            coverImageModel.setOrganizerId(organizerId);
            coverImageModel.setEventId(eventId);

            coverImageRepo.save(coverImageModel);

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

}
