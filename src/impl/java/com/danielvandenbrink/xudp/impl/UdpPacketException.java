package com.danielvandenbrink.xudp.impl;

class UdpPacketException extends RuntimeException {
    public UdpPacketException(String message) {
        super(message);
    }

    public UdpPacketException(Throwable cause) {
        super(cause);
    }

    public UdpPacketException(String message, Throwable cause) {
        super(message, cause);
    }
}
