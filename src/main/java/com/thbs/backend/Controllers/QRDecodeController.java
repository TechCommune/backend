package com.thbs.backend.Controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thbs.backend.Models.Event;
import com.thbs.backend.Repositories.EventProviderRepo;
import com.thbs.backend.Repositories.EventRepo;
import com.thbs.backend.Services.AESEncryptionService;
import com.thbs.backend.Services.AuthService;
// import com.thbs.backend.Services.DecodingService;
// import com.thbs.backend.Services.EncodeDecode;
// import com.thbs.backend.Services.RSAEncryptionService;

@RestController
@RequestMapping("api")
public class QRDecodeController {

    @Autowired
    private AESEncryptionService encryptDecryptService;
    // private EncodeDecode encodeDecode;

    @Autowired
    private AuthService authService;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private EventProviderRepo eventProviderRepo;
    private List<UUID> attendeeIds = new ArrayList<>();

    @GetMapping("scan-qr")
    public ResponseEntity<String> scanQRCode(@RequestParam String encryptedData,
                                             @RequestHeader String token) {
        try {
            String email = authService.verifyToken(token);
            UUID organizerId = eventProviderRepo.findByEmail(email).getId();

            // Decrypt the QR code data
            System.out.println("Encrypted data: " + encryptedData); // Debugging
            String encryptedData_without_space = encryptedData.replaceAll(" ", "+");
            System.out.println(encryptedData_without_space);
            String decryptedData = encryptDecryptService.decryptData(encryptedData_without_space);

            if (decryptedData == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to decrypt data");
            }

            System.out.println("Decrypted data: " + decryptedData); // Debugging

            String[] outputData = decryptedData.split(",");
            String user_id = outputData[0];
            String event_id = outputData[1];
            System.out.println("user_id : " + user_id + " event_id : " + event_id);

            // Further processing...

            return ResponseEntity.ok("Decryption successful user: " + user_id + " event: " + event_id);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update attendee list. Reason: " + e.getMessage());
        }
    }

    @GetMapping("/getAttendeeList")
    public List<UUID> getAttendeeList(@RequestHeader UUID eventId) {
        return attendeeIds;
    }

}