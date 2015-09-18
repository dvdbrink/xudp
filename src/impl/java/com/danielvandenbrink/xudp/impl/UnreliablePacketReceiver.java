package com.danielvandenbrink.xudp.impl;

import java.util.ArrayList;
import java.util.List;

class UnreliablePacketReceiver extends UdpPacketReceiver {
    public UnreliablePacketReceiver(EndpointState endpointState) {
        super(endpointState);
    }

    @Override
    public List<UdpPacket> receive(UdpPacket packet) {
        final List<UdpPacket> packets = new ArrayList<>();
        processAck(packet.ack(), packet.ackBitField());
        packets.add(packet);
        return packets;
    }
}
