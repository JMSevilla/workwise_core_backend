package com.workwise.springjwt.security.services;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Service
public class SecureKeyGenerator {
    public String generateHmacKey(int keyLengthBits, String passphrase) {
        try{
            SecureRandom secureRandom = new SecureRandom();
            byte[] salt = new byte[keyLengthBits / 8];
            secureRandom.nextBytes(salt);

            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec pbeKeySpec = new PBEKeySpec(passphrase.toCharArray(), salt, 10000, keyLengthBits);
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

            byte[] keyBytes = secretKey.getEncoded();
            return Base64.getEncoder().encodeToString(keyBytes);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e){
            throw new RuntimeException("Error generating HMAC Key", e);
        }
    }
}
