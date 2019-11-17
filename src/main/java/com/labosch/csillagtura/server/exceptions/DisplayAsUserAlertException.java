package com.labosch.csillagtura.server.exceptions;

public class DisplayAsUserAlertException extends RuntimeException {
    public DisplayAsUserAlertException(String message) {
        super(message);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
