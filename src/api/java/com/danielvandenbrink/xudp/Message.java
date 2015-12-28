package com.danielvandenbrink.xudp;

import java.net.SocketAddress;

public final class Message<T extends Packet> {
    private final T message;
    private final SocketAddress from;

    public Message(T message, SocketAddress from) {
        this.message = message;
        this.from = from;
    }

    public T packet() {
        return message;
    }

    public SocketAddress from() {
        return from;
    }
}