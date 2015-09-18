package com.danielvandenbrink.xudp.impl;

import java.util.ArrayList;
import java.util.List;

class ReliableUnorderedPacketReceiver extends UdpPacketReceiver {
    public ReliableUnorderedPacketReceiver(EndpointState endpointState) {
        super(endpointState);
    }

    @Override
    public List<UdpPacket> receive(UdpPacket packet) {
        final List<UdpPacket> packets = new ArrayList<>();
        processReliability(packet);
        processSequenceNumber(packet);
        packets.add(packet);
        return packets;
    }
}
