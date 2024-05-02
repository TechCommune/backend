package com.thbs.backend.Services;

import java.time.LocalDateTime;
import java.util.List;


import java.time.temporal.ChronoUnit;


import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thbs.backend.Models.AdminModel;
import com.thbs.backend.Models.EmailModel;
import com.thbs.backend.Models.EventProvider;
import com.thbs.backend.Models.LoginModel;
import com.thbs.backend.Models.OTPModel;
import com.thbs.backend.Models.ResponseMessage;
import com.thbs.backend.Models.UserModel;
import com.thbs.backend.Repositories.AdminRepo;
import com.thbs.backend.Repositories.EventProviderRepo;
import com.thbs.backend.Repositories.OtpRepo;
import com.thbs.backend.Repositories.UserRepo;
import com.thbs.backend.StaticInfo.OTPGenerator;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private EventProviderRepo eventProviderRepo;

    @Autowired
    private ResponseMessage responseMessage;

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailModel emailModel;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpRepo otpRepo;

    public String hashPassword(String password) {
        String strong_salt = BCrypt.gensalt(10);
        String encyptedPassword = BCrypt.hashpw(password, strong_salt);
        return encyptedPassword;
    }

    @Scheduled(fixedRate = 60000)
    public void deleteExpiredRecords() {
        LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(1).truncatedTo(ChronoUnit.MINUTES);
        List<OTPModel> expiredRecords = otpRepo.findByCreatedAt(expiryTime);
        if (expiredRecords.size() != 0) {
            otpRepo.deleteAll(expiredRecords);
        }
    }

    public ResponseEntity<Object> generateOTPforTwoFAService(UserModel userModel) {
        try {
            int otpForTwoFA = OTPGenerator.generateRandom6DigitNumber();
            OTPModel otp = new OTPModel();

            otp.setEmail(userModel.getEmail());
            otp.setOtp(otpForTwoFA);
            otp.setUseCase("login");
            otp.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

            emailModel.setRecipient(userModel.getEmail());
            emailModel.setSubject("OTP for Two-Factor Authentication");
            emailModel.setMsgBody("Your OTP for Two-Factor Authentication is " + otpForTwoFA
                    + ". It is valid only for 1 minutes.");

            String response = emailService.sendSimpleMail(emailModel);
            otpRepo.save(otp);
            responseMessage.setSuccess(true);
            responseMessage.setMessage(response);
            return ResponseEntity.ok().body(responseMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!");
        }
    }

    public ResponseEntity<Object> generateOTPforTwoFAServiceAdmin(AdminModel adminModel) {
        try {
            int otpForTwoFA = OTPGenerator.generateRandom6DigitNumber();
            OTPModel otp = new OTPModel();

            otp.setEmail(adminModel.getEmail());
            otp.setOtp(otpForTwoFA);
            otp.setUseCase("login");
            otp.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

            emailModel.setRecipient(adminModel.getEmail());
            emailModel.setSubject("OTP for Two-Factor Authentication");
            emailModel.setMsgBody("Your OTP for Two-Factor Authentication is " + otpForTwoFA
                    + ". It is valid only for 1 minutes.");

            String response = emailService.sendSimpleMail(emailModel);
            responseMessage.setSuccess(true);
            responseMessage.setMessage(response);
            otpRepo.save(otp);
            return ResponseEntity.ok().body(responseMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!");
        }
    }

    public ResponseEntity<Object> generateOTPforTwoFAServiceProviderService(EventProvider eventProvider) {
        try {
            int otpForTwoFA = OTPGenerator.generateRandom6DigitNumber();
            OTPModel otp = new OTPModel();

            otp.setEmail(eventProvider.getEmail());
            otp.setOtp(otpForTwoFA);
            otp.setUseCase("login");
            otp.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

            emailModel.setRecipient(eventProvider.getEmail());
            emailModel.setSubject("OTP for Two-Factor Authentication");
            emailModel.setMsgBody("Your OTP for Two-Factor Authentication is " + otpForTwoFA
                    + ". It is valid only for 1 minutes.");

            String response = emailService.sendSimpleMail(emailModel);
            responseMessage.setSuccess(true);
            responseMessage.setMessage(response);
            otpRepo.save(otp);
            return ResponseEntity.ok().body(responseMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!");
        }
    }

    public ResponseEntity<Object> userRegisterService(Object userOrService, BindingResult bindingResult, String role) {
        try {
            if (!bindingResult.hasErrors()) {
                if (bindingResult.hasFieldErrors()) {
                    responseMessage.setSuccess(false);
                    responseMessage.setMessage("Enter valid Email");
                    return ResponseEntity.ok().body(responseMessage);
                }

                if (role.equals("user")) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    UserModel userModel = objectMapper.convertValue(userOrService, UserModel.class);
                    UserModel userByEmail = userRepo.findByEmail(userModel.getEmail());
                    EventProvider eventproviderByEmail = eventProviderRepo
                           .findByEmail(userModel.getEmail());
                    if (userByEmail == null & eventproviderByEmail == null)  //to be edited
                    {
                        userModel.setPassword(hashPassword(userModel.getPassword()));
                        userRepo.save(userModel);
                        responseMessage.setSuccess(true);
                        responseMessage.setMessage("Account Created Successfully!");
                        responseMessage.setToken(authService.generateToken(userModel.getEmail()));
                        return ResponseEntity.ok().body(responseMessage);
                    } else {
                        responseMessage.setSuccess(false);
                        responseMessage.setMessage("User with this email already exists!");
                        responseMessage.setToken(null);
                        return ResponseEntity.ok().body(responseMessage);
                    }
                }

                else if (role.equals("eventprovider")) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    EventProvider eventProvider = objectMapper.convertValue(userOrService,
                            EventProvider.class);
                    EventProvider eventProvierByEmail = eventProviderRepo
                            .findByEmail(eventProvider.getEmail());

                    if (eventProvierByEmail == null) {
                        eventProvider.setPassword(hashPassword(eventProvider.getPassword()));
                        eventProviderRepo.save(eventProvider);
                        responseMessage.setSuccess(true);
                        responseMessage.setMessage("Account Created Successfully!");
                        responseMessage.setToken(authService.generateToken(eventProvider.getEmail()));
                        return ResponseEntity.ok().body(responseMessage);
                    } else {
                        responseMessage.setSuccess(false);
                        responseMessage.setMessage("User with this email already exists!");
                        responseMessage.setToken(null);
                        return ResponseEntity.ok().body(responseMessage);
                    }
                }

                else if (role.equals("admin")) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    AdminModel admin = objectMapper.convertValue(userOrService,
                            AdminModel.class);
            

                    if (adminRepo.count()==0) {
                        admin.setPassword(hashPassword(admin.getPassword()));
                        adminRepo.save(admin);
                        responseMessage.setSuccess(true);
                        responseMessage.setMessage("Admin Account Created Successfully!");
                        responseMessage.setToken(authService.generateToken(admin.getEmail()));
                        return ResponseEntity.ok().body(responseMessage);
                    } else {
                        responseMessage.setSuccess(false);
                        responseMessage.setMessage("Admin already exists!");
                        responseMessage.setToken(null);
                        return ResponseEntity.ok().body(responseMessage);
                    }
                }

                else {
                    responseMessage.setSuccess(false);
                    responseMessage.setMessage("Invalid Input!");
                    responseMessage.setToken(null);
                    return ResponseEntity.ok().body(responseMessage);
                }

            } else {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Invalid user name or email");
                return ResponseEntity.ok().body(responseMessage);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error!");
        }
    }

    public ResponseEntity<Object> userLoginService(LoginModel loginModel, String role) {
        try {
            if (role.equals("user")) {
                UserModel userModel = userRepo.findByEmail(loginModel.getEmail());
                if (userModel != null) {
                    if (BCrypt.checkpw(loginModel.getPassword(), userModel.getPassword())) {
                        responseMessage.setSuccess(true);
                        responseMessage.setMessage("Logged in Successfully!");
                        responseMessage.setToken(null);
                        generateOTPforTwoFAService(userModel);
                        return ResponseEntity.ok().body(responseMessage);
                    } else {
                        responseMessage.setSuccess(false);
                        responseMessage.setMessage("Invalid email or password");
                        responseMessage.setToken(null);
                        return ResponseEntity.ok().body(responseMessage);
                    }
                } else {
                    responseMessage.setSuccess(false);
                    responseMessage.setMessage("Invalid email or password");
                    return ResponseEntity.ok().body(responseMessage);
                }
            } 
            else if (role.equals("admin")){
                AdminModel adminModel = adminRepo.findByEmail(loginModel.getEmail());

                if (adminModel != null) {
                    if (BCrypt.checkpw(loginModel.getPassword(), adminModel.getPassword())) {
                        responseMessage.setSuccess(true);
                        responseMessage.setMessage(" Admin Logged in Successfully!");
                        responseMessage.setToken(null);
                        generateOTPforTwoFAServiceAdmin(adminModel);
                        return ResponseEntity.ok().body(responseMessage);
                    } else {
                        responseMessage.setSuccess(false);
                        responseMessage.setMessage("Invalid email or password");
                        responseMessage.setToken(null);
                        return ResponseEntity.ok().body(responseMessage);
                    }
                } else {
                    responseMessage.setSuccess(false);
                    responseMessage.setMessage("Invalid email or password");
                    return ResponseEntity.ok().body(responseMessage);
                }
            }

            else if (role.equals("eventprovider")){
                EventProvider eventProvider = eventProviderRepo.findByEmail(loginModel.getEmail());

                if (eventProvider != null) {
                    if (BCrypt.checkpw(loginModel.getPassword(), eventProvider.getPassword())) {
                        responseMessage.setSuccess(true);
                        responseMessage.setMessage("Logged in Successfully!");
                        responseMessage.setToken(null);
                        generateOTPforTwoFAServiceProviderService(eventProvider);
                        return ResponseEntity.ok().body(responseMessage);
                    } else {
                        responseMessage.setSuccess(false);
                        responseMessage.setMessage("Invalid email or password");
                        responseMessage.setToken(null);
                        return ResponseEntity.ok().body(responseMessage);
                    }
                } else {
                    responseMessage.setSuccess(false);
                    responseMessage.setMessage("Invalid email or password");
                    return ResponseEntity.ok().body(responseMessage);
                }
            }
            else {
                responseMessage.setSuccess(false);
                    responseMessage.setMessage("Invalid role");
                    return ResponseEntity.ok().body(responseMessage);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error!");
        }
    }

    public ResponseEntity<Object> TwoFAService(int otpforTwoFAFromUser, String email, String role) {
        try {
            int otpFromDB = otpRepo.findByEmail(email).getOtp();
            if (role.equals("user")) {
                if (otpFromDB == otpforTwoFAFromUser) {
                    responseMessage.setSuccess(true);
                    responseMessage.setMessage("Login Successfully!");
                    responseMessage.setToken(authService.generateToken(email));
                    return ResponseEntity.ok().body(responseMessage);
                } else {
                    responseMessage.setSuccess(false);
                    responseMessage.setMessage("Invalid OTP.");
                    return ResponseEntity.ok().body(responseMessage);
                }
            } else {
                if (otpFromDB == otpforTwoFAFromUser) {
                    responseMessage.setSuccess(true);
                    responseMessage.setMessage("Login Successfully!");
                    responseMessage.setToken(authService.generateToken(email));
                    return ResponseEntity.ok().body(responseMessage);
                } else {
                    responseMessage.setSuccess(false);
                    responseMessage.setMessage("Invalid OTP.");
                    return ResponseEntity.ok().body(responseMessage);
                }
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!");
        }
    }

    public ResponseEntity<Object> forgotPasswordService(String email, String role) {
        return sendingEmailService(email, role);
    }

    public ResponseEntity<Object> sendingEmailService(String email, String role) {
        try {
            if (role.equals("user")) {
                UserModel userByEmail = userRepo.findByEmail(email);
                if (userByEmail != null) {
                    int otp = OTPGenerator.generateRandom6DigitNumber();
                    OTPModel otpForForgotPassword = new OTPModel();
                    otpForForgotPassword.setEmail(email);
                    otpForForgotPassword.setOtp(otp);
                    otpForForgotPassword.setUseCase("forgotpassword");
                    otpForForgotPassword.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

                    otpRepo.save(otpForForgotPassword);

                    emailModel.setRecipient(email);
                    emailModel.setSubject("OTP for Resetting your password");
                    emailModel.setMsgBody("Your OTP for resetting your password is " + otp
                            + ". It is valid only for 5 minutes.");

                    String response = emailService.sendSimpleMail(emailModel);
                    responseMessage.setSuccess(true);
                    responseMessage.setMessage(response);
                    return ResponseEntity.ok().body(responseMessage);
                } else {
                    responseMessage.setSuccess(false);
                    responseMessage.setMessage("Invalid Email");
                    return ResponseEntity.ok().body(responseMessage);
                }
            } 
            else if (role.equals("eventprovider")) {
                EventProvider epByEmail = eventProviderRepo.findByEmail(email);
                if (epByEmail != null) {
                    int otp = OTPGenerator.generateRandom6DigitNumber();
                    OTPModel otpForForgotPassword = new OTPModel();
                    otpForForgotPassword.setEmail(email);
                    otpForForgotPassword.setOtp(otp);
                    otpForForgotPassword.setUseCase("forgotpassword");
                    otpForForgotPassword.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

                    emailModel.setRecipient(email);
                    emailModel.setSubject("OTP for Resetting your password");
                    emailModel.setMsgBody("Your OTP for resetting your password is " + Integer.toString(otp)
                            + ". It is valid only for 5 minutes.");

                    String response = emailService.sendSimpleMail(emailModel);
                    responseMessage.setSuccess(true);
                    responseMessage.setMessage(response);
                    return ResponseEntity.ok().body(responseMessage);
                } else {
                    responseMessage.setSuccess(false);
                    responseMessage.setMessage("Invalid Email");
                    return ResponseEntity.ok().body(responseMessage);
                }
            } 
            else {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Invalid role");
                return ResponseEntity.ok().body(responseMessage);
            }
            

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!");
        }
    }

    public ResponseEntity<Object> verifyTheOtpEnteredByUser(int otpFromUser, String email) {
        try {
            OTPModel otpFromDB = otpRepo.findByEmail(email);
            if (otpFromDB.getOtp() == otpFromUser) {
                responseMessage.setSuccess(true);
                responseMessage.setMessage("OTP Verified");
                return ResponseEntity.ok().body(responseMessage);
            } else {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Invalid OTP, check your registered Email to get the 6-digit OTP");
                return ResponseEntity.ok().body(responseMessage);
            }
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage("Internal Server Error in verifyTheOtpEnteredByUser. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    public ResponseEntity<Object> resetThePasswordService(String passwordFromUser, String role,String email) {
        try {
            if (role.equals("user")) {
                UserModel user = userRepo.findByEmail(email);
                user.setPassword(hashPassword(passwordFromUser));
                userRepo.save(user);

                responseMessage.setSuccess(true);
                responseMessage.setMessage("Password Changed Successfully");
                return ResponseEntity.ok().body(responseMessage);
            } 
            else {
                EventProvider eventProvider = eventProviderRepo
                        .findByEmail(email);
                eventProvider.setPassword(hashPassword(passwordFromUser));
                eventProviderRepo.save(eventProvider);

                responseMessage.setSuccess(true);
                responseMessage.setMessage("Password Changed Successfully");
                return ResponseEntity.ok().body(responseMessage);
            }
            

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!");
        }
    }

    public ResponseEntity<Object> getUserDetailsByEmailService(String token, String role) {
        try {
            String email = authService.verifyToken(token);
            if (role.equals("user")) {
                UserModel user = userRepo.findByEmail(email);
                if (user != null) {
                    return ResponseEntity.ok(user);
                } else {
                    responseMessage.setSuccess(false);
                    responseMessage.setMessage("Invalid email");
                    return ResponseEntity.ok().body(responseMessage);
                }
            } 
            else {
                EventProvider eventProvider = eventProviderRepo.findByEmail(email);
                if (eventProvider != null) {
                    return ResponseEntity.ok(eventProvider);
                } else {
                    responseMessage.setSuccess(false);
                    responseMessage.setMessage("Invalid email");
                    return ResponseEntity.ok().body(responseMessage);
                }
            }
           
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!");
        }
    }
}

