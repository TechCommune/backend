package com.thbs.backend.Controllers;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thbs.backend.Models.EventEnrollment;
import com.thbs.backend.Repositories.EventEnrollRepo;
import com.thbs.backend.Services.EventProviderVerificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/admin")
public class AdminController {


    @Autowired
    private EventEnrollRepo eventEnrollRepo;
    
    @Autowired
    private EventProviderVerificationService eventProviderVerificationService;

    @PostMapping("/approveEventProvider")
    public ResponseEntity<String> approveEventProvider(@RequestHeader String adminToken, @RequestHeader UUID organizerId) {
        eventProviderVerificationService.approveEventProvider(adminToken,organizerId);
        return ResponseEntity.ok("Event Provider approved");
    }

    @PostMapping("/denyEventProvider")
    public ResponseEntity<String> denyEventProvider(@RequestHeader String adminToken, @RequestHeader UUID organizerId) {
        eventProviderVerificationService.denyEventProvider(adminToken,organizerId);
        return ResponseEntity.ok("Event Provider denied");
    }

    @GetMapping("getallenrollments")
    public List<EventEnrollment> getAllEnrollments() {
        return eventEnrollRepo.findAll();
    }
    
}
