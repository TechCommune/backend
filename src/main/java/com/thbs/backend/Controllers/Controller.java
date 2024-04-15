package com.thbs.backend.Controllers;

import java.util.List;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thbs.backend.Models.Event;
import com.thbs.backend.Models.EventDetails;
import com.thbs.backend.Models.EventEnrollment;
import com.thbs.backend.Models.LoginModel;
import com.thbs.backend.Models.ResponseMessage;
import com.thbs.backend.Services.AddEventDetails;
import com.thbs.backend.Services.EventEnrollmentService;
import com.thbs.backend.Services.GetEventAndUpdate;
import com.thbs.backend.Services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api")
public class Controller {

    @Autowired
    private UserService userService;

    @Autowired
    private AddEventDetails addEventDetails;

    @Autowired
    private GetEventAndUpdate getEventAndUpdate;

    @Autowired
    private EventEnrollmentService enrollmentService;

    @PostMapping("adduser")
    public ResponseEntity<Object> addUser(@Valid @RequestBody Object userOrService, BindingResult bindingResult,
            @RequestHeader String role) {
        return userService.userRegisterService(userOrService, bindingResult, role);
    }

    @GetMapping("getuserdetailsbytoken")
    public ResponseEntity<Object> getUserDetailsByToken(@RequestHeader String token, @RequestHeader String role) {
        return userService.getUserDetailsByEmailService(token, role);
    }

    @GetMapping("login")
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
    public List<Event> getAllEvent(){
        return getEventAndUpdate.getAllEvent();
    }
    @GetMapping("getevent")
    public Event updateEvent(@RequestHeader UUID eventId) {
        return getEventAndUpdate.getEvent(eventId);
    }

    @PutMapping("updateevent")
    public ResponseEntity<ResponseMessage> updateEvent(@RequestHeader UUID eventId, @RequestHeader String role,
            @RequestHeader String token, @RequestBody Event eventToBeUpdated) {
        return getEventAndUpdate.updateEvent(token, role, eventId, eventToBeUpdated);
    }

    @DeleteMapping("deleteevent")
    public ResponseEntity<ResponseMessage> deleteEvent(@RequestHeader UUID eventId,@RequestHeader String role, @RequestHeader String token)
    {
        return getEventAndUpdate.deleteEvent(token, role, eventId);
    }

    @PostMapping("enroll")
    public ResponseEntity<ResponseMessage> enrollUser(@RequestBody EventEnrollment eventEnrollment,
            @RequestHeader String token) {
        return enrollmentService.enrollUser(eventEnrollment, token);
    }

    @GetMapping("getallenroll")
    public List<EventEnrollment> getAEnrollments(@RequestHeader UUID eventId)
    {
        return enrollmentService.getEnrollmentsByEventId(eventId);
    }

    @GetMapping("getuserenroll")
    public List<EventEnrollment> getUserEnrolls(@RequestHeader UUID userId)
    {
        return enrollmentService.getEnrollmentsByUserId(userId);
    }
}
