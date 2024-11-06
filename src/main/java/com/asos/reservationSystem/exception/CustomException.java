package com.asos.reservationSystem.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private String loggingMessage;

    public CustomException() {
    }

    public CustomException(String message, String loggingMessage) {
        super(message);
        this.loggingMessage = loggingMessage;
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(Throwable cause) {
        super(cause);
    }
}
