package com.thbs.backend.Controllers;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thbs.backend.Services.EventProviderVerificationService;

@RestController
@RequestMapping("/admin")
public class AdminController {

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
}
