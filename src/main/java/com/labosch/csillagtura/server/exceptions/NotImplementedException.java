package com.labosch.csillagtura.server.exceptions;

public class NotImplementedException extends RuntimeException {
    public NotImplementedException(String message) {
        super(message);
    }
    public NotImplementedException() {
        super();
    }
}
