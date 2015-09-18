package com.danielvandenbrink.xudp.impl;

class UdpSocketException extends RuntimeException {
    public UdpSocketException(String message) {
        super(message);
    }

    public UdpSocketException(Throwable cause) {
        super(cause);
    }

    public UdpSocketException(String message, Throwable cause) {
        super(message, cause);
    }
}
