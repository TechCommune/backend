package com.thbs.backend.Services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import com.thbs.backend.Models.EventEnrollment;
import com.thbs.backend.Models.ResponseMessage;
import com.thbs.backend.Repositories.EventEnrollRepo;
import com.thbs.backend.Repositories.EventRepo;
import com.thbs.backend.Repositories.UserRepo;

@Service
public class EventEnrollmentService {

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private EventEnrollRepo eventEnrollmentRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ResponseMessage responseMessage;

    
    public ResponseEntity<ResponseMessage> enrollUser(EventEnrollment eventEnrollment, String token) {
        try {
            String email = authService.verifyToken(token);
            String userId = userRepo.findByEmail(email).getId().toString();
    
            // Check if the event exists
            if (!eventRepo.existsById(eventEnrollment.getEventId())) {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Event Not Found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(responseMessage);
            }
    
            // Check if the event has reached its maximum capacity
            int enrolledUsers = (int) eventEnrollmentRepo.countByEventId(eventEnrollment.getEventId());
            int maxCapacity = eventRepo.findById(eventEnrollment.getEventId()).get().getMaxCapacity();
            if (enrolledUsers >= maxCapacity) {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Event has reached maximum capacity");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(responseMessage);
            }
    
            // Enroll the user for the event
            EventEnrollment eventEnroll = new EventEnrollment();
            eventEnroll.setUserId(UUID.fromString(userId));
            eventEnroll.setEventId(eventEnrollment.getEventId());
            eventEnroll.setEnrollmentDate(eventEnrollment.getEnrollmentDate()); // Assuming Date class is available for date handling
            eventEnroll.setPrice(eventEnrollment.getPrice());
            eventEnroll.setPaymentRequired(eventEnrollment.isPaymentRequired());
            eventEnrollmentRepo.save(eventEnroll);

            responseMessage.setSuccess(true);
            responseMessage.setMessage("User Enrolled Successfully");
            return ResponseEntity.ok().body(responseMessage);
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }
    public List<EventEnrollment> getEnrollmentsByUserId(UUID userId) {
        return eventEnrollmentRepo.findByUserId(userId);
    }

    public List<EventEnrollment> getEnrollmentsByEventId(UUID eventId) {
        return eventEnrollmentRepo.findByEventId(eventId);
    }

    
}
