package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.Packet;
import com.danielvandenbrink.xudp.PacketEvent;
import com.danielvandenbrink.xudp.PacketEventFactory;

import java.net.SocketAddress;

public class UdpPacketEventFactory implements PacketEventFactory {
    @Override
    public PacketEvent create(Packet packet, SocketAddress address) {
        return new UdpPacketEvent(packet, address);
    }
}
