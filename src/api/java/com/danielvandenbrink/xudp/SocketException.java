package com.danielvandenbrink.xudp;

public class SocketException extends RuntimeException {
    public SocketException(String message) {
        super(message);
    }

    public SocketException(Throwable cause) {
        super(cause);
    }

    public SocketException(String message, Throwable cause) {
        super(message, cause);
    }
}
