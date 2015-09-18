package com.danielvandenbrink.xudp.impl;

import java.util.List;

abstract class UdpPacketSender {
    private final EndpointState endpointState;

    protected UdpPacketSender(EndpointState endpointState) {
        this.endpointState = endpointState;
    }

    public EndpointState connection() {
        return endpointState;
    }

    protected int generateAckBitField(int ack) {
        int ackBitField = 0;

        for (final UdpPacket packet : connection().receivedPackets()) {
            final int sequence = packet.sequence();

            assert sequence <= ack;
            if (sequence == ack) {
                break;
            }

            final int bitIndex = ack - 1 - sequence;
            ackBitField |= 1 << bitIndex;
        }

        return ackBitField;
    }

    public abstract List<UdpPacket> send(UdpPacket packet);
}
