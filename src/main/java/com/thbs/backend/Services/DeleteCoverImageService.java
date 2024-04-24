package com.thbs.backend.Services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.thbs.backend.Models.CoverImageModel;
import com.thbs.backend.Models.ResponseMessage;
import com.thbs.backend.Repositories.CoverImageRepo;
import com.thbs.backend.StaticData.S3Data;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
public class DeleteCoverImageService {

    @Autowired
    private ResponseMessage responseMessage;

    @Autowired
    private S3PutObjectService s3PutObjectService;

    @Autowired
    private CoverImageRepo coverImageRepo;

    public ResponseEntity<ResponseMessage> deleteImageService(CoverImageModel imageInfo) {
        S3Client client = S3Data.s3Client;
        try {
            String bucketName = System.getenv("BUCKET_NAME");
            String serviceProviderFolder=System.getenv("FOLDER_FOR_EVENTS");
            String key = serviceProviderFolder + '/'+imageInfo.getEventId().toString() + '/' + imageInfo.getCoverImageId();
            if (s3PutObjectService.checkObjectInBucket(bucketName, key)) {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();

                client.deleteObject(deleteObjectRequest);

                coverImageRepo.deleteById(imageInfo.getCoverImageId());

                responseMessage.setSuccess(true);
                responseMessage.setMessage("Object '" + key + "' deletion success!");

                return ResponseEntity.status(HttpStatus.OK).body(responseMessage);

            } else {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Object '" + key + "' not found");

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            }
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage
                    .setMessage("Internal Server Error in DeleteImageService.java. Method: deleteImageService. Reason: "
                            + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);

        }
    }
}