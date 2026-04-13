package com.example.springaicrud.validation;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class InputValidator {

    // ✅ Email pattern
    private static final Pattern EMAIL =
            Pattern.compile(
                    "^[A-Za-z0-9+_.-]+@"
                            + "[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // ✅ Mobile number pattern
    private static final Pattern MOBILE =
            Pattern.compile("^[6-9]\\d{9}$");

    // ✅ Name pattern (letters and spaces only)
    private static final Pattern NAME =
            Pattern.compile(
                    "^[A-Za-z\\s]{2,100}$");

    // ✅ Password pattern
    // min 6 chars, at least one number
    private static final Pattern PASSWORD =
            Pattern.compile(
                    "^(?=.*[0-9]).{6,50}$");

    // ✅ Validate email
    public void validateEmail(String email) {
        if (email == null
                || email.trim().isEmpty()) {
            throw new RuntimeException(
                    "Email is required!");
        }
        if (email.length() > 255) {
            throw new RuntimeException(
                    "Email too long!");
        }
        if (!EMAIL.matcher(email).matches()) {
            throw new RuntimeException(
                    "Invalid email format! "
                            + "Use: example@email.com");
        }
    }

    // ✅ Validate password
    public void validatePassword(
            String password) {
        if (password == null
                || password.trim().isEmpty()) {
            throw new RuntimeException(
                    "Password is required!");
        }
        if (password.length() < 6) {
            throw new RuntimeException(
                    "Password must be at least "
                            + "6 characters!");
        }
        if (password.length() > 50) {
            throw new RuntimeException(
                    "Password too long! "
                            + "Max 50 characters.");
        }
        if (!PASSWORD.matcher(password)
                .matches()) {
            throw new RuntimeException(
                    "Password must contain "
                            + "at least one number!");
        }
    }

    // ✅ Validate name
    public void validateName(String name) {
        if (name == null
                || name.trim().isEmpty()) {
            throw new RuntimeException(
                    "Name is required!");
        }
        if (name.length() < 2) {
            throw new RuntimeException(
                    "Name too short! "
                            + "Min 2 characters.");
        }
        if (name.length() > 100) {
            throw new RuntimeException(
                    "Name too long! "
                            + "Max 100 characters.");
        }
        if (!NAME.matcher(name).matches()) {
            throw new RuntimeException(
                    "Name must contain "
                            + "letters only!");
        }
    }

    // ✅ Validate mobile number
    public void validateMobile(String mobile) {
        if (mobile == null
                || mobile.trim().isEmpty()) {
            throw new RuntimeException(
                    "Mobile number is required!");
        }
        if (!MOBILE.matcher(mobile).matches()) {
            throw new RuntimeException(
                    "Invalid mobile number! "
                            + "Must be 10 digits "
                            + "starting with 6-9.");
        }
    }

    // ✅ Validate general text input
    public void validateText(
            String value,
            String fieldName,
            int maxLength) {
        if (value == null
                || value.trim().isEmpty()) {
            throw new RuntimeException(
                    fieldName + " is required!");
        }
        if (value.length() > maxLength) {
            throw new RuntimeException(
                    fieldName + " too long! "
                            + "Max " + maxLength
                            + " characters.");
        }
        // check for SQL injection
        if (containsSqlInjection(value)) {
            throw new RuntimeException(
                    "Invalid characters in "
                            + fieldName + "!");
        }
    }

    // ✅ Validate price
    public void validatePrice(
            java.math.BigDecimal price) {
        if (price == null) {
            throw new RuntimeException(
                    "Price is required!");
        }
        if (price.compareTo(
                java.math.BigDecimal.ZERO) < 0) {
            throw new RuntimeException(
                    "Price cannot be negative!");
        }
        if (price.compareTo(
                new java.math.BigDecimal(
                        "99999.99")) > 0) {
            throw new RuntimeException(
                    "Price too high! "
                            + "Max ₹99,999.99");
        }
    }

    // ✅ Check for SQL injection patterns
    private boolean containsSqlInjection(
            String input) {
        String[] patterns = {
                "select", "insert", "update",
                "delete", "drop", "create",
                "alter", "exec", "union",
                "script", "--", "/*", "*/",
                "xp_", "char(", "nchar("
        };

        String lower = input.toLowerCase();
        for (String pattern : patterns) {
            if (lower.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    // ✅ Sanitize input
    // remove dangerous characters
    public String sanitize(String input) {
        if (input == null) return null;
        return input
                .trim()
                .replaceAll(
                        "[<>\"'%;()&+]", "")
                .replaceAll(
                        "\\s+", " ");
    }
}