package com.thbs.backend.Services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

import com.thbs.backend.Models.Event;
import com.thbs.backend.Models.EventDetails;
import com.thbs.backend.Models.EventProvider;
import com.thbs.backend.Models.ResponseMessage;
import com.thbs.backend.Repositories.EventProviderRepo;
import com.thbs.backend.Repositories.EventRepo;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AddEventDetails {

    @Autowired
    private EventProviderRepo eventProviderRepo;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private AuthService authService;

    public ResponseEntity<ResponseMessage> addEvent(List<EventDetails> eventdetails, String token, String role) {
        ResponseMessage responseMessage = new ResponseMessage();

        try {
            String email = authService.verifyToken(token);
            EventProvider eventProvider = eventProviderRepo.findByEmail(email);
            if (eventProvider == null) {
                throw new RuntimeException("Event Provider not found");
            }
            if ("Denied".equals(eventProvider.getVerificationApproval()) || "Pending".equals(eventProvider.getVerificationApproval())) {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Admin Approval Pending");
                return ResponseEntity.ok().body(responseMessage);
            }
            String eventOrgId = eventProvider.getId().toString();
            StringBuilder duplicatesMessage = new StringBuilder();

            for (EventDetails eventDetail : eventdetails) {
                Event eventExistence = eventRepo.findByEventOrgIdAndEventTitle(UUID.fromString(eventOrgId), eventDetail.getTitle());

                if (eventExistence == null) {
                    Event event = new Event();
                    event.setEventOrgId(UUID.fromString(eventOrgId));
                    event.setTitle(eventDetail.getTitle());
                    event.setDescription(eventDetail.getDescription());
                    event.setLocation(eventDetail.getLocation());
                    event.setMode(eventDetail.getMode());
                    event.setStartTime(eventDetail.getStartTime());
                    event.setEndTime(eventDetail.getEndTime());
                    event.setMaxCapacity(eventDetail.getMaxCapacity());
                    event.setPrice(eventDetail.getPrice());
                    event.setPaymentRequired(eventDetail.isPaymentRequired());
                    eventRepo.save(event);
                } else if (eventExistence.getTitle().equals(eventDetail.getTitle())) {
                    // Event exists with the same exact title
                    duplicatesMessage.append(eventDetail.getTitle()).append(", ");
                }
            }

            if (duplicatesMessage.length() == 0) {
                responseMessage.setSuccess(true);
                responseMessage.setMessage("Event registered successfully");
            } else {
                responseMessage.setSuccess(false);
                // Remove the trailing comma and space
                duplicatesMessage.setLength(duplicatesMessage.length() - 2);
                responseMessage.setMessage("Event Title(s): " + duplicatesMessage.toString() + " already exist(s), hence not been added.");
            }
            return ResponseEntity.ok().body(responseMessage);

        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }
}
