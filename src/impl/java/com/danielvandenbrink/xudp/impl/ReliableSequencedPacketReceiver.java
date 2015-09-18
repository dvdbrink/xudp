package com.danielvandenbrink.xudp.impl;

import java.util.ArrayList;
import java.util.List;

class ReliableSequencedPacketReceiver extends UdpPacketReceiver {
    public ReliableSequencedPacketReceiver(EndpointState endpointState) {
        super(endpointState);
    }

    @Override
    public List<UdpPacket> receive(UdpPacket packet) {
        final List<UdpPacket> packets = new ArrayList<>();
        processReliability(packet);
        if (processSequenceNumber(packet)) {
            packets.add(packet);
        }
        return packets;
    }
}
