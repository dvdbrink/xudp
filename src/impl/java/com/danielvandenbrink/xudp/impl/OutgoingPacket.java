package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.Packet;

import java.net.SocketAddress;

class OutgoingPacket<T extends Packet> {
    private final T message;
    private final SocketAddress address;

    public OutgoingPacket(T message, SocketAddress address) {
        this.message = message;
        this.address = address;
    }

    public T packet() {
        return message;
    }

    public SocketAddress address() {
        return address;
    }
}