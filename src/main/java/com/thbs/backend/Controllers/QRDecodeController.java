package com.thbs.backend.Controllers;
 
import java.util.List;
import java.util.Optional;
import java.util.UUID;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
 
import com.thbs.backend.Models.AttendeeList;
import com.thbs.backend.Models.Event;
import com.thbs.backend.Models.UserModel;
import com.thbs.backend.Repositories.AttendeeRepo;
import com.thbs.backend.Repositories.EventProviderRepo;
import com.thbs.backend.Repositories.EventRepo;
import com.thbs.backend.Repositories.UserRepo;
import com.thbs.backend.Services.AESEncryptionService;
import com.thbs.backend.Services.AuthService;
 
@RestController
@CrossOrigin(origins = "http://localhost:5173")

@RequestMapping("api")
public class QRDecodeController {
 
    @Autowired
    private AESEncryptionService encryptDecryptService;
 
    @Autowired
    private AuthService authService;
 
    @Autowired
    private AttendeeRepo attendeeRepo;
 
    @Autowired
    private UserRepo userRepo;
 
    @Autowired
    private EventRepo eventRepo;
 
    @Autowired
    private EventProviderRepo eventProviderRepo;
   
 
    @GetMapping("scan-qr")
    public ResponseEntity<String> scanQRCode(@RequestParam String encryptedData,
            @RequestHeader String token) {
        try {
            String email = authService.verifyToken(token);
            UUID organizerId = eventProviderRepo.findByEmail(email).getId();
 
            // Decrypt the QR code data
            String encryptedData_without_space = encryptedData.replaceAll(" ", "+");
            String decryptedData = encryptDecryptService.decryptData(encryptedData_without_space);
 
            if (decryptedData == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to decrypt data");
            }
 
 
            String[] outputData = decryptedData.split(",");
            String user_id = outputData[0];
            String event_id = outputData[1];
 
 
            Event eventFromDB = eventRepo.findByEventId(UUID.fromString(event_id));
            if (!eventFromDB.getEventOrgId().equals(organizerId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Event ID is not valid for the organizer");
            }
           
           
 
            Optional<UserModel> userDetails = userRepo.findById(UUID.fromString(user_id));
            if (userDetails.isPresent()) {
                UserModel user = userDetails.get();
                // Create attendee object
                AttendeeList attendee = new AttendeeList();
                attendee.setUserName(user.getUserName());
                attendee.setEmail(user.getEmail());
                attendee.setEventId(UUID.fromString(event_id));
                attendeeRepo.save(attendee);
 
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
 
 
            return ResponseEntity.ok("Decryption successful user: " + user_id + " event: " + event_id);
 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update attendee list. Reason: " + e.getMessage());
        }
    }
 
    @GetMapping("/getAttendeeList")
    public List<AttendeeList> getAttendeeList(@RequestHeader UUID eventId) {
        return attendeeRepo.findByEventId(eventId);
    }
 
}