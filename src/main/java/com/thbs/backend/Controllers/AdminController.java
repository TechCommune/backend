package com.thbs.backend.Controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thbs.backend.Models.EventEnrollment;
import com.thbs.backend.Models.ResponseMessage;
import com.thbs.backend.Repositories.EventEnrollRepo;
import com.thbs.backend.Services.EventProviderVerificationService;

@RestController
@CrossOrigin(origins = "http://3.111.246.82:5173")

@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EventEnrollRepo eventEnrollRepo;

    @Autowired
    private EventProviderVerificationService eventProviderVerificationService;

    @Autowired
    private ResponseMessage responseMessage;

    @PostMapping("/approveEventProvider")
    public ResponseEntity<ResponseMessage> approveEventProvider(@RequestHeader String adminToken,
            @RequestHeader UUID organizerId) {
        eventProviderVerificationService.approveEventProvider(adminToken, organizerId);
        responseMessage.setSuccess(true);
        responseMessage.setMessage("Event Provider approved");
        return ResponseEntity.status(200).body(responseMessage);
    }

    @PostMapping("/denyEventProvider")
    public ResponseEntity<ResponseMessage> denyEventProvider(@RequestHeader String adminToken, @RequestHeader UUID organizerId) {
        eventProviderVerificationService.denyEventProvider(adminToken, organizerId);
        responseMessage.setSuccess(true);
        responseMessage.setMessage("Event Provider denied");
        return ResponseEntity.status(200).body(responseMessage);
    }

    @GetMapping("getallenrollments")
    public List<EventEnrollment> getAllEnrollments() {
        return eventEnrollRepo.findAll();
    }

}