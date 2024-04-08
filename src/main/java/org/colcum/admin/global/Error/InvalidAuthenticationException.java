package org.colcum.admin.global.Error;

public class InvalidAuthenticationException extends RuntimeException {

    public InvalidAuthenticationException() {
        super("Invalid authentication.");
    }

    public InvalidAuthenticationException(String message) {
        super(message);
    }

    public InvalidAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAuthenticationException(Throwable cause) {
        super(cause);
    }
}
