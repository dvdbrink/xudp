package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.Packet;
import com.danielvandenbrink.xudp.PacketEvent;

import java.net.SocketAddress;

public class UdpPacketEvent implements PacketEvent {
    private final Packet packet;
    private final SocketAddress from;

    public UdpPacketEvent(Packet packet, SocketAddress from) {
        this.packet = packet;
        this.from = from;
    }

    public final Packet packet() {
        return packet;
    }

    public final SocketAddress from() {
        return from;
    }
}
