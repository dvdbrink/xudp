package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.PacketException;

class UdpPacketException extends PacketException {
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
