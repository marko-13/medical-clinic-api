package com.proj.medicalClinic.exception;

public class NotExistsException extends RuntimeException {

    public NotExistsException() {
        super();
    }

    public NotExistsException(String message, Throwable cause, boolean enableSuppression,
                              boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public NotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistsException(String message) {
        super(message);
    }

    public NotExistsException(Throwable cause) {
        super(cause);
    }
}