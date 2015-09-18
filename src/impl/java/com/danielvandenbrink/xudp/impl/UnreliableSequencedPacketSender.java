package com.danielvandenbrink.xudp.impl;

import java.util.ArrayList;
import java.util.List;

class UnreliableSequencedPacketSender extends UdpPacketSender {
    public UnreliableSequencedPacketSender(EndpointState endpointState) {
        super(endpointState);
    }

    @Override
    public List<UdpPacket> send(UdpPacket packet) {
        final List<UdpPacket> packets = new ArrayList<>();
        packet.ack(connection().sequenceState().remote());
        packet.ackBitField(generateAckBitField(connection().sequenceState().remote()));
        packet.sequence(connection().sequenceState().next());
        packets.add(packet);
        return packets;
    }
}
