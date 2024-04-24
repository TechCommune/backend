package com.thbs.backend.Services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.thbs.backend.Repositories.EventRepo;
import com.thbs.backend.Repositories.UserRepo;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class QRCodeMailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private UserRepo userRepo;

    public void sendEmailWithAttachment(byte[] pdfData, String recipientEmail, UUID eventId)
            throws MessagingException, jakarta.mail.MessagingException {

        String eventName = eventRepo.findByEventId(eventId).getTitle();
        String userName = userRepo.findByEmail(recipientEmail).getUserName();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(recipientEmail);
        helper.setSubject("QR Code PDF Attachment");
        helper.setText("Hii " + userName + ", \n\n Please find the QR E-Ticket for Event : " + eventName
                + "  attached to this email. This QR code will serve as your ticket for entry into the event venue.\n\nThank you.");

        // Set the PDF attachment
        helper.addAttachment("qrcode.pdf", new ByteArrayResource(pdfData));

        javaMailSender.send(message);
    }
}