package com.thbs.backend.Services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;
import com.thbs.backend.Repositories.EventProviderRepo;
import com.thbs.backend.Models.Event;
import com.thbs.backend.Models.ResponseMessage;
import com.thbs.backend.Repositories.EventRepo;

@Service
public class GetEventAndUpdate {

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private EventProviderRepo eventProviderRepo;

    @Autowired
    private ResponseMessage responseMessage;

    public List<Event> getAllEvent() {
        return eventRepo.findAll();
    }

    public Event getEvent(UUID eventId) {
        return eventRepo.findByEventId(eventId);
    }

    public ResponseEntity<ResponseMessage> updateEvent(String token, String role, UUID eventId,
            Event eventToBeUpdated) {
        try {

            String email = authService.verifyToken(token);
            String event_org_id = eventProviderRepo.findByEmail(email).getId().toString();
            Event eventFetched = getEvent(eventId);
            if (event_org_id.equals(eventFetched.getEventOrgId().toString())) {

                eventFetched.setTitle(eventToBeUpdated.getTitle());
                eventFetched.setDescription(eventToBeUpdated.getDescription());
                eventFetched.setLocation(eventToBeUpdated.getLocation());
                eventFetched.setMode(eventToBeUpdated.getMode());
                eventFetched.setStartTime(eventToBeUpdated.getStartTime());
                eventFetched.setEndTime(eventToBeUpdated.getEndTime());
                eventFetched.setMaxCapacity(eventToBeUpdated.getMaxCapacity());
                eventFetched.setPrice(eventToBeUpdated.getPrice());
                eventFetched.setPaymentRequired(eventToBeUpdated.isPaymentRequired());

                eventRepo.save(eventFetched);
                responseMessage.setSuccess(true);
                responseMessage.setMessage("Event details updated successfully");
                return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
            } else {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Event with ID: " + eventFetched.getEventId()
                        + " and event Organizer with Id: " + eventFetched.getEventOrgId() + "doesn't exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            }

        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage("Internal Server Error in method updateEventDetails. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }

    }

    public ResponseEntity<ResponseMessage> deleteEvent(String token, String role, UUID eventId) {
        try {

            String email = authService.verifyToken(token);
            String eventOrgId = eventProviderRepo.findByEmail(email).getId().toString();
            Event event = eventRepo.findByEventId(eventId);

            if (event == null) {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Event with ID: " + eventId + " not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
            }

            if (!event.getEventOrgId().toString().equals(eventOrgId)) {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("You do not have permission to delete this event");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMessage);
            }
            eventRepo.delete(event);
            responseMessage.setSuccess(true);
            responseMessage.setMessage("Event deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage("Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

}
