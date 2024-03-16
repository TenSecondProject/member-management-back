package org.colcum.admin.global.Error;

public class EmailValidationException extends RuntimeException {

    public EmailValidationException() {
        super("Invalid email address.");
    }

    public EmailValidationException(String message) {
        super(message);
    }

    public EmailValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailValidationException(Throwable cause) {
        super(cause);
    }
}
