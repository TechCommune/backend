package com.thbs.backend.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class QRCodeMailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmailWithAttachment(byte[] pdfData, String recipientEmail)throws MessagingException, jakarta.mail.MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(recipientEmail);
        helper.setSubject("QR Code PDF Attachment");
        helper.setText("Please find the attached PDF.");
        // Set the PDF attachment
        helper.addAttachment("qrcode.pdf", new ByteArrayResource(pdfData));

        javaMailSender.send(message);
    }
}