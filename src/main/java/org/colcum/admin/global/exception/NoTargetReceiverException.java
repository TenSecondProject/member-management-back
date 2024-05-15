package org.colcum.admin.global.exception;

public class NoTargetReceiverException extends RuntimeException {

    public NoTargetReceiverException() {
        super("No target receivers");
    }

    public NoTargetReceiverException(String message) {
        super(message);
    }

    public NoTargetReceiverException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoTargetReceiverException(Throwable cause) {
        super(cause);
    }

}
