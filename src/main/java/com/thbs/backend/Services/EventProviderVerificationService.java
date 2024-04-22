package com.thbs.backend.Services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.thbs.backend.Models.AdminModel;
import com.thbs.backend.Models.EmailModel;
import com.thbs.backend.Models.EventProvider;
import com.thbs.backend.Repositories.AdminRepo;
import com.thbs.backend.Repositories.EventProviderRepo;

import jakarta.validation.constraints.Email;

public class EventProviderVerificationService {
    @Autowired
    private EventProviderRepo eventProviderRepo;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailModel emailModel;

    @Autowired
    private AuthService authService;

    public void approveEventProvider(String adminToken, UUID organizerId) {
        try {
            String email = authService.verifyToken(adminToken);
            AdminModel admin = adminRepo.findByEmail(email);
            if (admin == null) {
                throw new RuntimeException("Admin Token Mismatch");
            }

            EventProvider eventProvider = eventProviderRepo.findById(organizerId)
                    .orElseThrow(() -> new RuntimeException("Event Provider not found"));

            String event_organizer_mail = eventProvider.getEmail();
            emailModel.setRecipient(event_organizer_mail);
            emailModel.setSubject("Verification Update for TechCommune Event Organizer");
            emailModel.setMsgBody("Hi,\n\n" +
                    "We are pleased to inform you that your document verification has been successful. You are now approved to create events.");

            eventProvider.setVerificationApproval(true);
            eventProviderRepo.save(eventProvider);

        } catch (Exception e) {
            throw new RuntimeException("Failed to approve event provider: " + e.getMessage());
        }
    }

    public void denyEventProvider(String adminToken, UUID organizerId) {
        try {
            String email = authService.verifyToken(adminToken);
            AdminModel admin = adminRepo.findByEmail(email);
            if (admin == null) {
                throw new RuntimeException("Admin Token Mismatch");
            }

            EventProvider eventProvider = eventProviderRepo.findById(organizerId)
                    .orElseThrow(() -> new RuntimeException("Event Provider not found"));

            String event_organizer_mail = eventProvider.getEmail();
            emailModel.setRecipient(event_organizer_mail);
            emailModel.setSubject("Verification Update for TechCommune Event Organizer");
            emailModel.setMsgBody("Hi,\n\n" +
                    "We regret to inform you that your document verification has been unsuccessful. As a result, you are not approved to create events at this time. ");

            eventProvider.setVerificationApproval(false);
            eventProviderRepo.save(eventProvider);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deny event provider: " + e.getMessage());
        }
    }

}
