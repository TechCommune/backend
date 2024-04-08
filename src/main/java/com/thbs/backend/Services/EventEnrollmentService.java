package com.thbs.backend.Services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thbs.backend.Models.Event;
import com.thbs.backend.Models.EventEnrollment;
import com.thbs.backend.Models.ResponseMessage;
import com.thbs.backend.Repositories.EventEnrollRepo;
import com.thbs.backend.Repositories.EventRepo;

@Service
public class EventEnrollmentService {

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private EventEnrollRepo eventEnrollmentRepo;

    public ResponseMessage enrollUser(UUID userId, UUID eventId, int price, boolean paymentRequired) {
        ResponseMessage response = new ResponseMessage();
        Event event = eventRepo.findById(eventId).orElse(null);
        if (event == null) {
            response.setSuccess(false);
            response.setMessage("Event with ID: " + eventId + " not found");
            return response;
        }

        // Check if there is available capacity for additional participants
        if (event.getMaxCapacity() <= eventEnrollmentRepo.countByEventId(eventId)) {
            response.setSuccess(false);
            response.setMessage("Event is already full. Cannot enroll more participants.");
            return response;
        }

        // Perform any additional checks before enrolling the user, such as checking if the event is full, etc.
        // For simplicity, let's assume the enrollment is always successful here.
        EventEnrollment enrollment = new EventEnrollment();
        enrollment.setUserId(userId);
        enrollment.setEventId(eventId);
        enrollment.setPrice(price);
        enrollment.setPaymentRequired(paymentRequired);

        eventEnrollmentRepo.save(enrollment);

        response.setSuccess(true);
        response.setMessage("User enrolled successfully for the event");
        return response;
    }
    public List<EventEnrollment> getEnrollmentsByUserId(UUID userId) {
        return eventEnrollmentRepo.findByUserId(userId);
    }

    public List<EventEnrollment> getEnrollmentsByEventId(UUID eventId) {
        return eventEnrollmentRepo.findByEventId(eventId);
    }

    
}
