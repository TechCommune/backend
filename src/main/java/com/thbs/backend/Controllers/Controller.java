package com.thbs.backend.Controllers;

import java.time.LocalDateTime;
import java.util.List;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.thbs.backend.Models.CoverImageModel;
import com.thbs.backend.Models.Event;
import com.thbs.backend.Models.EventDetails;
import com.thbs.backend.Models.EventDetailsDTO;
import com.thbs.backend.Models.EventEnrollment;
import com.thbs.backend.Models.EventProvider;
import com.thbs.backend.Models.LoginModel;
import com.thbs.backend.Models.ResponseMessage;
import com.thbs.backend.Models.ReviewDetails;
import com.thbs.backend.Repositories.EventProviderRepo;
import com.thbs.backend.Repositories.EventRepo;
import com.thbs.backend.Services.AddEventDetails;
import com.thbs.backend.Services.CoverImageUploadService;
import com.thbs.backend.Services.DeleteCoverImageService;
import com.thbs.backend.Services.EventEnrollmentService;
import com.thbs.backend.Services.EventRating;
import com.thbs.backend.Services.FetchCoverImage;
import com.thbs.backend.Services.FetchImages;
import com.thbs.backend.Services.GetEventAndUpdate;
import com.thbs.backend.Services.ImageUploadService;
import com.thbs.backend.Services.ReviewService;
import com.thbs.backend.Services.UserService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("api")
public class Controller {

    @Autowired
    private UserService userService;

    @Autowired
    private FetchImages fetchImages;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private AddEventDetails addEventDetails;

    @Autowired
    private GetEventAndUpdate getEventAndUpdate;

    @Autowired
    private EventEnrollmentService enrollmentService;

    @Autowired
    private EventRating eventRating;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CoverImageUploadService coverImageUploadService;

    @Autowired
    private DeleteCoverImageService deleteCoverImagesService;

    @Autowired
    private EventProviderRepo eventProviderRepo;

    @Autowired
    private FetchCoverImage fetchCoverImage;

    @PostMapping("adduser")
    public ResponseEntity<Object> addUser(@Valid @RequestBody Object userOrService, BindingResult bindingResult,
            @RequestHeader String role) {
        return userService.userRegisterService(userOrService, bindingResult, role);
    }

    @GetMapping("getuserdetailsbytoken")
    public ResponseEntity<Object> getUserDetailsByToken(@RequestHeader String token, @RequestHeader String role) {
        return userService.getUserDetailsByEmailService(token, role);
    }

    @PostMapping("login")
    public ResponseEntity<Object> verifyUser(@RequestBody LoginModel loginModel, @RequestHeader String role) {
        return userService.userLoginService(loginModel, role);
    }

    @PostMapping("2fa")
    public ResponseEntity<Object> twofa(@RequestHeader int otpforTwoFAFromUser, @RequestHeader String email,
            @RequestHeader String role) {
        return userService.TwoFAService(otpforTwoFAFromUser, email, role);
    }

    @PostMapping("forgotpassword")
    public ResponseEntity<Object> forgotPassword(@RequestHeader String email, @RequestHeader String role) {
        return userService.forgotPasswordService(email, role);
    }

    @PostMapping("verifyOtpforforgotpassword")
    public ResponseEntity<Object> verifyTheUserOtp(@RequestHeader int otp, @RequestHeader String email) {
        return userService.verifyTheOtpEnteredByUser(otp, email);
    }

    @PostMapping("resetpassword")
    public ResponseEntity<Object> resetThePassword(@RequestHeader String passwordFromUser, @RequestHeader String role,
            @RequestHeader String email) {
        return userService.resetThePasswordService(passwordFromUser, role, email);
    }

    @PostMapping("addevent")
    public ResponseEntity<ResponseMessage> addEvent(@RequestHeader String role, @RequestHeader String token,
            @RequestBody List<EventDetails> eventdetails) {
        return addEventDetails.addEvent(eventdetails, token, role);
    }

    @GetMapping("getallevent")
    public List<Event> getAllEvent() {
        return getEventAndUpdate.getAllEvent();
    }

    @GetMapping("getevent")
    public ResponseEntity<Object> getEvent(@RequestHeader UUID eventId) {
        return getEventAndUpdate.getEvent(eventId);
    }

    @PutMapping("updateevent")
    public ResponseEntity<ResponseMessage> updateEvent(@RequestHeader UUID eventId, @RequestHeader String role,
            @RequestHeader String token, @RequestBody Event eventToBeUpdated) {
        return getEventAndUpdate.updateEvent(token, role, eventId, eventToBeUpdated);
    }

    @DeleteMapping("deleteevent")
    public ResponseEntity<ResponseMessage> deleteEvent(@RequestHeader UUID eventId, @RequestHeader String role,
            @RequestHeader String token) {
        return getEventAndUpdate.deleteEvent(token, role, eventId);
    }

    @PostMapping("enroll")
    public ResponseEntity<ResponseMessage> enrollUser(@RequestBody EventEnrollment eventEnrollment,
            @RequestHeader String token) {
        return enrollmentService.enrollUser(eventEnrollment, token);
    }

    @PostMapping("/cancelEnrollment")
    public ResponseEntity<ResponseMessage> cancelEnrollment(@RequestHeader String token,
            @RequestHeader UUID eventId) {

        return enrollmentService.cancelEnrollment(token, eventId);
    }

    @GetMapping("getallenroll")
    public List<EventEnrollment> getAEnrollments(@RequestHeader UUID eventId) {
        return enrollmentService.getEnrollmentsByEventId(eventId);
    }

    @GetMapping("getuserenroll")
    public List<EventEnrollment> getUserEnrolls(@RequestHeader UUID userId) {
        return enrollmentService.getEnrollmentsByUserId(userId);
    }

    @PostMapping("addeventrating")
    public ResponseEntity<ResponseMessage> addEventRating(@RequestHeader String eventOrgId,
            @RequestHeader String eventId, @RequestHeader float rating) {
        return eventRating.addEventRating(eventOrgId, eventId, rating);
    }

    @PostMapping("addreview")
    public ResponseEntity<ResponseMessage> addReview(@RequestHeader String token,
            @RequestBody ReviewDetails reviewDetails) {
        return reviewService.addReview(token, reviewDetails);
    }

    @GetMapping("getreview")
    public ResponseEntity<Object> getReview(@RequestHeader String eventId) {
        return reviewService.getReview(eventId);
    }

    @PutMapping("updatereview")
    public ResponseEntity<ResponseMessage> updateReview(@RequestHeader String token,
            @RequestBody ReviewDetails reviewDetails) {
        return reviewService.updateReview(token, reviewDetails);
    }

    @DeleteMapping("deletereview")
    public ResponseEntity<ResponseMessage> deleteReview(@RequestHeader String token, @RequestHeader UUID reviewId) {
        return reviewService.deleteReview(token, reviewId);
    }

    @PostMapping("uploadimages")
    public ResponseEntity<ResponseMessage> uploadImages(@RequestHeader String token, @RequestHeader String role,
            @RequestBody List<MultipartFile> images) {
        return imageUploadService.uploadImageService(token, role, images);
    }

    @GetMapping("getimagesforep")
    public ResponseEntity<Object> getAllImagesForSP(@RequestHeader String adminToken, @RequestHeader UUID organizerId) {
        return fetchImages.fetchImagesService(adminToken, organizerId);
    }

    @DeleteMapping("deletecoverimage")
    public ResponseEntity<ResponseMessage> deleteImage(@RequestBody CoverImageModel imageInfo) {
        return deleteCoverImagesService.deleteImageService(imageInfo);
    }

    @PostMapping("addcoverimage")
    public ResponseEntity<ResponseMessage> AddCoverImageForEvent(@RequestHeader String EPToken,
            @RequestHeader UUID event_id, @RequestBody MultipartFile images) {

        return coverImageUploadService.uploadCoverImageService(EPToken, event_id, images);
    }

    @GetMapping("getalleventproviders")
    public ResponseEntity<List<EventProvider>> getAllEventProviders() {
        List<EventProvider> eventProviders = eventProviderRepo.findAll();
        return new ResponseEntity<>(eventProviders, HttpStatus.OK);
    }

    @GetMapping("getunapprovedeventproviders")
    public ResponseEntity<List<EventProvider>> getUnApprovedEventProviders() {
        List <EventProvider> eventProviders = eventProviderRepo.findByVerificationApproval("Pending");
        return new ResponseEntity<>(eventProviders, HttpStatus.OK);
    }
    

    @GetMapping("fetchcoverimage")
    public ResponseEntity<Object> FetchCoverImage(@RequestHeader UUID organizerId) {
        return fetchCoverImage.fetchImagesService(organizerId);
    }
    

    @GetMapping("getalleventbyorgid")
    public List<Event> getAllEventsByOrgId(@RequestHeader UUID organizerId) {
        return eventRepo.findByEventOrgId(organizerId);
    }

    @GetMapping("searchevents")
    public List<Event> searchEventsUsingTopicName(@RequestParam String topic) {
        return eventRepo.findByTitleContainingIgnoreCase(topic);
    }

    @GetMapping("filterevents")
    public List<Event> filterEvents(@RequestParam String location, @RequestParam String mode) {
        if (location != null && mode != null) {
            return eventRepo.findByLocationContainingIgnoreCaseAndModeContainingIgnoreCase(location, mode);
        } else if (location != null) {
            return eventRepo.findByLocationContainingIgnoreCase(location);
        } else if (mode != null) {
            return eventRepo.findByModeContainingIgnoreCase(mode);
        } else {
            return eventRepo.findAll();
        }
    }

    @GetMapping("getenrollmentsbytoken")
    public List<EventEnrollment> getEnrolls(@RequestHeader String token) {
        return enrollmentService.getEnrollmentsByToken(token);

    }

     @GetMapping("/completed")
    public List<EventDetailsDTO> getCompletedEventsWithMaxCapacity(@RequestHeader UUID organizerId) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Event> completedEvents = eventRepo.findByEventOrgIdAndEndTimeBefore(organizerId , currentDateTime);
        return completedEvents.stream()
                .map(event -> new EventDetailsDTO(event, event.getMaxCapacity()))
                .collect(Collectors.toList());
    }

    @GetMapping("/upcoming")
    public List<Event> getUpcomingEvents(@RequestHeader UUID organizerId) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return eventRepo.findByEventOrgIdAndStartTimeAfterOrderByStartTimeAsc(organizerId , currentDateTime);
    }

    @GetMapping("eventcoverimage")
    public ResponseEntity<Object> FetchImageByEventId(@RequestHeader UUID eventId) {
        return fetchCoverImage.fetchImageByEventId(eventId);
    }

}