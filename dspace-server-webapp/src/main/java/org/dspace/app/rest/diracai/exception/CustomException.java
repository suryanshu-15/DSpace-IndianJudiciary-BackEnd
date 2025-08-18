package org.dspace.app.rest.diracai.exception;


public class CustomException extends RuntimeException {
    private final int statusCode;

    public CustomException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
