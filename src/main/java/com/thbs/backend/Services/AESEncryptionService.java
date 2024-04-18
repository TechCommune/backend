package com.thbs.backend.Services;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class AESEncryptionService {

    private SecretKey secretKey;
    private IvParameterSpec ivParameterSpec;

    public AESEncryptionService() {
        this.secretKey = generateAESKey();
        this.ivParameterSpec = generateIV();
    }

    public String encryptData(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryptData(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private SecretKey generateAESKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256); // Use a 256-bit key for better security
            return keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private IvParameterSpec generateIV() {
        byte[] iv = new byte[16]; // AES block size is 128 bits (16 bytes)
        // You may want to use a secure random generator to generate the IV
        return new IvParameterSpec(iv);
    }
}