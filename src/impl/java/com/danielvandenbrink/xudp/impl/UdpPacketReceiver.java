package com.danielvandenbrink.xudp.impl;

import java.util.Iterator;
import java.util.List;

abstract class UdpPacketReceiver {
    private final EndpointState endpointState;

    protected UdpPacketReceiver(EndpointState endpointState) {
        this.endpointState = endpointState;
    }

    public EndpointState connection() {
        return endpointState;
    }

    protected boolean processSequenceNumber(UdpPacket packet) {
        if (isSequenceMoreRecent(packet.sequence(), connection().sequenceState().remote())) {
            connection().sequenceState().remote(packet.sequence());
            return true;
        }
        return false;
    }

    protected void processReliability(UdpPacket packet) {
        processAck(packet.ack(), packet.ackBitField());
        connection().receivedPackets(packet);
    }

    protected void processAck(int ack, int ackBitField) {
        final Iterator<UdpPacket> packets = connection().pendingAcks().iterator();
        while (packets.hasNext()) {
            final int sequence = packets.next().sequence();
            boolean acked = false;

            if (sequence == ack) {
                acked = true;
            } else if (!isSequenceMoreRecent(sequence, ack)) {
                final int bitIndex = ack - 1 - sequence;
                acked = ((ackBitField >> bitIndex) & 1) == 1;
            }

            if (acked) {
                packets.remove();
            }
        }
    }

    private boolean isSequenceMoreRecent(int sequence, int ack) {
        return ack == -1
                || (sequence > ack) && (sequence - ack <= (SequenceState.MAX_SEQUENCE_NUMBER + 1) / 2)
                || (ack > sequence) && (ack - sequence > (SequenceState.MAX_SEQUENCE_NUMBER + 1) / 2);
    }

    public abstract List<UdpPacket> receive(UdpPacket packet);
}
