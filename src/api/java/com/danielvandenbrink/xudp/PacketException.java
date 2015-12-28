package com.danielvandenbrink.xudp;

public class PacketException extends RuntimeException {
    public PacketException(String message) {
        super(message);
    }

    public PacketException(Throwable cause) {
        super(cause);
    }

    public PacketException(String message, Throwable cause) {
        super(message, cause);
    }
}
