package com.proj.medicalClinic.exception;

public class NotValidParamsException extends RuntimeException {

    public NotValidParamsException() {
        super();
    }

    public NotValidParamsException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public NotValidParamsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotValidParamsException(String message) {
        super(message);
    }

    public NotValidParamsException(Throwable cause) {
        super(cause);
    }

}
