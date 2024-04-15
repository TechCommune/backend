package com.thbs.backend.Services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.thbs.backend.Models.Event;
import com.thbs.backend.Models.ResponseMessage;
import com.thbs.backend.Repositories.EventRepo;

@Service
public class EventRating {

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private ResponseMessage responseMessage;

    public ResponseEntity<ResponseMessage> eventrating(String eventOrgId, String eventId, float rating) {
        try {
            if (rating > 5 || rating < 0) {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Rating cannot exceed 5 and cannot be less than 0");
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(responseMessage);
            } else {
                Event eventDetails = eventRepo.findByEventOrgIdAndEventId(UUID.fromString(eventOrgId),
                        UUID.fromString(eventId));

                if (eventDetails != null) {

                    if (eventDetails.getNumberOfRatings() == 0) {
                        eventDetails.setRating(Math.round(rating * 10.0) / 10.0);
                        eventDetails.setRatingSum(rating);
                        eventDetails.setNumberOfRatings(1);
                    } else {
                        int totalNumberOfRatings = eventDetails.getNumberOfRatings() + 1;
                        eventDetails.setRatingSum(eventDetails.getRatingSum() + rating);
                        eventDetails.setNumberOfRatings(totalNumberOfRatings);
                        eventDetails.setRating(
                                Math.round((eventDetails.getRatingSum() / totalNumberOfRatings) * 10.0) / 10.0);
                    }

                    eventRepo.save(eventDetails);

                    responseMessage.setSuccess(true);
                    responseMessage
                            .setMessage(
                                    "Rating added and number of ratings are: " + eventDetails.getNumberOfRatings());
                } else {
                    responseMessage.setSuccess(false);
                    responseMessage.setMessage("Invalid Event Organizer ID");
                    return ResponseEntity.badRequest().body(responseMessage);
                }

                return ResponseEntity.status(HttpStatus.OK).body(responseMessage);

            }
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage(
                    "Internal Server Error in EventRating.java."
                            + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);

        }

    }

}
