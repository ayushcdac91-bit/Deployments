package com.statewide.login.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ChangePasswordUtil {

    public static final String PASSWORD_STRENGTH_NO = "0";
    public static final String PASSWORD_STRENGTH_LOW = "1";
    public static final String PASSWORD_STRENGTH_MEDIUM = "2";
    public static final String PASSWORD_STRENGTH_HIGH = "3";

    public static final String PASSWORD_LOW_MESSAGE = "Password must contain at least one Alphabet.";
    public static final String PASSWORD_MEDIUM_MESSAGE = "Password must contain at least one Alphabet and one Number.";
    public static final String PASSWORD_HIGH_MESSAGE = "Password must contain at least one Alphabet, one Number and one Special Character.";

    // Set required strength
    public static final String PASSWORD_STRENGTH = PASSWORD_STRENGTH_HIGH;

    public Map<String, Object> checkPassStrength(String password, String userName) {

        Map<String, Object> response = new HashMap<>();
        boolean valid = true;

        // Basic allowed characters
        String regexAll = "[0-9a-zA-Z.&_!~`@#$%^&*:<>?]+";
        boolean isAllPresent = password.matches(regexAll);

        boolean hasAlpha = password.matches(".*[A-Za-z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[.&_!~`@#$%^&*:<>?].*");

        if (!isAllPresent) {
            valid = false;
            response.put("message", "Password contains invalid characters!");
        }

        if (PASSWORD_STRENGTH.equals(PASSWORD_STRENGTH_LOW) && !hasAlpha) {
            valid = false;
            response.put("message", PASSWORD_LOW_MESSAGE);
        }

        if (PASSWORD_STRENGTH.equals(PASSWORD_STRENGTH_MEDIUM) && !(hasAlpha && hasNumber)) {
            valid = false;
            response.put("message", PASSWORD_MEDIUM_MESSAGE);
        }

        if (PASSWORD_STRENGTH.equals(PASSWORD_STRENGTH_HIGH)
                && !(hasAlpha && hasNumber && hasSpecial)) {
            valid = false;
            response.put("message", PASSWORD_HIGH_MESSAGE);
        }

        // Username presence check
        if (password.toUpperCase().contains(userName.toUpperCase())) {
            valid = false;
            response.put("message", "Password must not contain User Name!");
        }

        // Common sequences
        String[] commonSeq = { "CDAC", "PASSWORD", "QWERTY" };
        for (String seq : commonSeq) {
            if (password.toUpperCase().contains(seq)) {
                valid = false;
                response.put("message",
                        "Password must not contain known common sequence like '" + seq + "'!");
            }
        }

        response.put("valid", valid);
        return response;
    }
}
