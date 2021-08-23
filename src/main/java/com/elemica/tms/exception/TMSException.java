package com.elemica.tms.exception;

public class TMSException extends RuntimeException {

    private static final long serialVersionUID = -514406192720810498L;

    private final String message;

    public TMSException(String message) {

        this.message = message;
    }

    public String getMessage() {

        return message;
    }
}
