package com.galvanize.prodman.exception;

import org.springframework.http.HttpStatus;

public class InvalidCurrencyException extends Exception {

    private int status;

    public InvalidCurrencyException(String message, int status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return HttpStatus.valueOf(this.status);
    }
}
