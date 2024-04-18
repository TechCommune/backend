package com.thbs.backend.Services;



import com.itextpdf.io.IOException;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PDFService {

    public byte[] createPDFFromQRCode(byte[] qrCodeImage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Create an image from the QR code byte array
        Image qrCode = new Image(ImageDataFactory.create(qrCodeImage));

        // Add the image to the PDF document
        document.add(qrCode);

        // Close the document
        document.close();

        return baos.toByteArray();
    }
}