package com.danielvandenbrink.xudp.impl;

import java.util.ArrayList;
import java.util.List;

class UnreliablePacketSender extends UdpPacketSender {
    public UnreliablePacketSender(EndpointState endpointState) {
        super(endpointState);
    }

    @Override
    public List<UdpPacket> send(UdpPacket packet) {
        final List<UdpPacket> packets = new ArrayList<>();
        packet.ack(connection().sequenceState().remote());
        packet.ackBitField(generateAckBitField(connection().sequenceState().remote()));
        packets.add(packet);
        return packets;
    }
}
