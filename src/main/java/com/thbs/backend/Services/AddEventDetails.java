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
    private ResponseMessage responseMessage;

    @Autowired
    private AuthService authService;

    public ResponseEntity<ResponseMessage> addEvent(List<EventDetails> eventdetails, String token, String role) {

        try {
            String email = authService.verifyToken(token);
            EventProvider eventProvider = eventProviderRepo.findByEmail(email);
            if (eventProvider == null) {
                throw new RuntimeException("Event Provider not found");
            }
            if (eventProvider.getVerificationApproval().equals("Denied")
                    || eventProvider.getVerificationApproval().equals("Pending")) {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Admin Approval Pending");
                return ResponseEntity.ok().body(responseMessage);
            }
            String event_org_id = eventProvider.getId().toString();
            String duplicatesMessage = "";

            for (int i = 0; i < eventdetails.size(); i++) {
                Event eventExistence = eventRepo.findByEventOrgIdAndEventTitle(UUID.fromString(event_org_id),
                        eventdetails.get(i).getTitle());

                Event event = new Event();

                if (eventExistence == null) {
                    event.setEventOrgId(UUID.fromString(event_org_id));
                    event.setTitle(eventdetails.get(i).getTitle());
                    event.setDescription(eventdetails.get(i).getDescription());
                    event.setLocation(eventdetails.get(i).getLocation());
                    event.setMode(eventdetails.get(i).getMode());
                    event.setStartTime(eventdetails.get(i).getStartTime());
                    event.setEndTime(eventdetails.get(i).getEndTime());
                    event.setMaxCapacity(eventdetails.get(i).getMaxCapacity());
                    event.setPrice(eventdetails.get(i).getPrice());
                    event.setPaymentRequired(eventdetails.get(i).isPaymentRequired());
                    eventRepo.save(event);
                } else {
                    duplicatesMessage += eventRepo.findByTitle(eventdetails.get(i).getTitle()).getTitle();
                    duplicatesMessage += ", ";
                }
            }
            if (duplicatesMessage.length() == 0) {
                responseMessage.setSuccess(true);
                responseMessage.setMessage("Event registered successfully");
                return ResponseEntity.ok().body(responseMessage);
            } else {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Event Title " + duplicatesMessage
                        + " already exists, hence not been added.");
                return ResponseEntity.ok().body(responseMessage);
            }

        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }
}
