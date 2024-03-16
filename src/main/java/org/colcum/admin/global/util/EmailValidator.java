package org.colcum.admin.global.util;

import org.colcum.admin.global.Error.EmailValidationException;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class EmailValidator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static void validate(String email) {
        if (!StringUtils.hasText(email)) {
            throw new EmailValidationException("Email must not be empty.");
        }

        if (email.length() > 50) {
            throw new EmailValidationException("Email is too long.");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new EmailValidationException("Invalid email format.");
        }
    }

}
