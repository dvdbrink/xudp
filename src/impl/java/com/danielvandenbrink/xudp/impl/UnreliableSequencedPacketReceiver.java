package com.danielvandenbrink.xudp.impl;

import java.util.ArrayList;
import java.util.List;

class UnreliableSequencedPacketReceiver extends UdpPacketReceiver {
    public UnreliableSequencedPacketReceiver(EndpointState endpointState) {
        super(endpointState);
    }

    @Override
    public List<UdpPacket> receive(UdpPacket packet) {
        final List<UdpPacket> packets = new ArrayList<>();
        processAck(packet.ack(), packet.ackBitField());
        if (processSequenceNumber(packet)) {
            packets.add(packet);
        }
        return packets;
    }
}
