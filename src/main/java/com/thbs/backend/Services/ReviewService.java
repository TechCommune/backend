package com.thbs.backend.Services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.thbs.backend.Models.Event;
import com.thbs.backend.Models.ResponseMessage;
import com.thbs.backend.Models.ReviewDetails;
import com.thbs.backend.Repositories.EventEnrollRepo;
import com.thbs.backend.Repositories.EventRepo;
import com.thbs.backend.Repositories.ReviewRepo;
import com.thbs.backend.Repositories.UserRepo;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private ResponseMessage responseMessage;

    @Autowired
    private AuthService authService;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private EventEnrollRepo eventEnrollmentRepo;

    @Autowired
    private UserRepo userRepo;

    public ResponseEntity<ResponseMessage> addReview(@RequestHeader String token,
            @RequestBody ReviewDetails reviewDetails) {
        try {

            String email = authService.verifyToken(token);
            String userId = userRepo.findByEmail(email).getId().toString();

            UUID eventId = reviewDetails.getEventId();

            // Check if the event exists in the database
            Optional<Event> eventOptional = eventRepo.findById(eventId);
            if (eventOptional.isEmpty()) {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Event not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            }
            // Check if the user is enrolled in the event
            boolean isEnrolled = eventEnrollmentRepo.existsByUserIdAndEventId(UUID.fromString(userId), eventId);
            if (!isEnrolled) {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("You need to enroll in the event before writing a review");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
            }

            ReviewDetails reviewProvided = new ReviewDetails();

            reviewProvided.setEventId(reviewDetails.getEventId());
            reviewProvided.setUserId(reviewDetails.getUserId());
            reviewProvided.setUserName(reviewDetails.getUserName());
            reviewProvided.setReview(reviewDetails.getReview());
            // Save the review to the database
            reviewRepo.save(reviewProvided);

            responseMessage.setSuccess(true);
            responseMessage.setMessage("Review added successfully");

            return ResponseEntity.ok().body(responseMessage);
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage("Failed to add review. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

   
    public ResponseEntity<Object> getReview(String eventId) {
        try {
            List<ReviewDetails> fetchedReview = reviewRepo.findByEventId(UUID.fromString(eventId));
            if (!fetchedReview.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(fetchedReview);

            } else {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("No Reviews");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);

            }
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }

    }

    public ResponseEntity<ResponseMessage> updateReview(String token, ReviewDetails updatedReviewDetails) {
        try {
            // Verify user token
            String email = authService.verifyToken(token);
            String userId = userRepo.findByEmail(email).getId().toString();
            UUID reviewId = updatedReviewDetails.getReviewId();

            // Fetch the review from the database
            Optional<ReviewDetails> reviewOptional = reviewRepo.findById(reviewId);
            if (reviewOptional.isEmpty()) {
                // Review with the given ID not found
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Review not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            }

            // Check if the user is the author of the review
            ReviewDetails existingReview = reviewOptional.get();
            if (!existingReview.getUserId().toString().equals(userId)) {
                // User is not authorized to update this review
                responseMessage.setSuccess(false);
                responseMessage.setMessage("You are not authorized to update this review");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMessage);
            }

            // Update the review details
            existingReview.setReview(updatedReviewDetails.getReview());

            // Save the updated review to the database
            reviewRepo.save(existingReview);

            // Success response
            responseMessage.setSuccess(true);
            responseMessage.setMessage("Review updated successfully");
            return ResponseEntity.ok().body(responseMessage);
        } catch (Exception e) {
            // Error handling
            responseMessage.setSuccess(false);
            responseMessage.setMessage("Failed to update review. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    public ResponseEntity<ResponseMessage> deleteReview(String token, UUID reviewId) {
        try {
            String email = authService.verifyToken(token);
            String userId = userRepo.findByEmail(email).getId().toString();

            // Check if the review exists
            Optional<ReviewDetails> existingReviewOptional = reviewRepo.findById(reviewId);
            if (existingReviewOptional.isEmpty()) {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Review not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            }

            // Check if the user is authorized to delete the review
            ReviewDetails existingReview = existingReviewOptional.get();
            if (!existingReview.getUserId().equals(UUID.fromString(userId))) {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("You are not authorized to delete this review");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMessage);
            }

            // Delete the review
            reviewRepo.deleteById(reviewId);

            responseMessage.setSuccess(true);
            responseMessage.setMessage("Review deleted successfully");
            return ResponseEntity.ok().body(responseMessage);
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage("Failed to delete review. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }
}
