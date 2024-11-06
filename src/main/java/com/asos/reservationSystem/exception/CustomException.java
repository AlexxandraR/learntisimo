package com.asos.reservationSystem.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException{
    private String loggingMessage;
    private HttpStatus status;

    public CustomException() {
    }

    public CustomException(String message, String loggingMessage, HttpStatus status) {
        super(message);
        this.loggingMessage = loggingMessage;
        this.status = status;
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(Throwable cause) {
        super(cause);
    }
}
