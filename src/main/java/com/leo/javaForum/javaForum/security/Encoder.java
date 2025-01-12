package com.leo.javaForum.javaForum.security;


import com.leo.javaForum.javaForum.logger.AppLogger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class Encoder {
    private static Logger logger = AppLogger.getLogger();

    public static String hashPassword(String password) {
        String hashedPassword = null;
        logger.info("Hashing password: " + password);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            hashedPassword = hexString.toString();
            logger.info("Hashed password: " + hashedPassword);
        } catch (NoSuchAlgorithmException ignored) {
            logger.warning("No such algorithm");
        }

        return hashedPassword;
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        logger.info("Verifying password: " + password);
        String computedHash = hashPassword(password);
        return computedHash.equals(hashedPassword);
    }
}
