package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.PacketFactory;
import com.danielvandenbrink.xudp.Protocol;

class UdpPacketFactory implements PacketFactory<UdpPacket> {
    @Override
    public UdpPacket create(Protocol protocol, byte[] data) {
        return new UdpPacket(protocol, data);
    }
}
