package com.galvanize.prodman.exception;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends Exception {

    private int status;

    public ProductNotFoundException(String message, int status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return HttpStatus.valueOf(this.status);
    }
}