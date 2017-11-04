package com.tesis.yudith.showmethepast.exceptions;

public class RequestException extends Exception {
    private int statusCode;

    public RequestException(int statusCode, String errorMessage) {
        super(errorMessage);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
