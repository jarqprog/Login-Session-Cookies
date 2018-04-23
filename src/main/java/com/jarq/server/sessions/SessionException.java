package com.jarq.server.sessions;

public class SessionException extends Exception {

    private String message;

    SessionException() {
        message = "problem with session occurred";
    }

    SessionException(String message) {
        this.message = message;
    }

}
