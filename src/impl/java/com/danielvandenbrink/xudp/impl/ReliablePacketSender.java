package com.danielvandenbrink.xudp.impl;

import java.util.ArrayList;
import java.util.List;

class ReliablePacketSender extends UdpPacketSender {
    public ReliablePacketSender(EndpointState endpointState) {
        super(endpointState);
    }

    @Override
    public List<UdpPacket> send(UdpPacket packet) {
        final List<UdpPacket> packets = new ArrayList<>();
        packet.ack(connection().sequenceState().remote());
        packet.ackBitField(generateAckBitField(connection().sequenceState().remote()));
        packet.sequence(connection().sequenceState().next());
        packet.timestamp(System.currentTimeMillis());
        for (final UdpPacket pendingAck : connection().pendingAcks()) {
            packets.add(pendingAck);
        }
        connection().pendingAcks().add(packet);
        packets.add(packet);
        return packets;
    }
}
