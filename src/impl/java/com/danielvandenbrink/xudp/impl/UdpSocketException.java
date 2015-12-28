package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.SocketException;

class UdpSocketException extends SocketException {
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
