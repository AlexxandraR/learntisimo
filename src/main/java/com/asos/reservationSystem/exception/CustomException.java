package com.asos.reservationSystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private String loggingMessage;
    private HttpStatus status;

    public CustomException(String message, String loggingMessage, HttpStatus status) {
        super(message);
        this.loggingMessage = loggingMessage;
        this.status = status;
    }
}
