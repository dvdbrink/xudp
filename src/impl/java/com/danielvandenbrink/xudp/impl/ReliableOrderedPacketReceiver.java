package com.danielvandenbrink.xudp.impl;

import java.util.ArrayList;
import java.util.List;

class ReliableOrderedPacketReceiver extends UdpPacketReceiver {
    public ReliableOrderedPacketReceiver(EndpointState endpointState) {
        super(endpointState);
    }

    @Override
    public List<UdpPacket> receive(UdpPacket packet) {
        final List<UdpPacket> packets = new ArrayList<>();
        processReliability(packet);
        processSequenceNumber(packet);
        if (check(packet.sequence(), connection().sequenceState().nextOrdered())) {
            connection().pendingOrderedPackets().add(packet);
            if (isNextOrderedSequence(packet.sequence())) {
                final List<UdpPacket> nextOrderedPackets = getNextOrderedPackets(packet.sequence());
                for (final UdpPacket nextOrderedPacket : nextOrderedPackets) {
                    packets.add(nextOrderedPacket);
                }
                connection().sequenceState().nextOrdered(packets.get(packets.size() - 1).sequence() + 1);
            }
        }
        return packets;
    }

    private boolean check(int sequence, int ack) {
        return (sequence >= ack) && (sequence - ack < (SequenceState.MAX_SEQUENCE_NUMBER + 1) / 2)
                || (ack > sequence) && (ack - sequence > (SequenceState.MAX_SEQUENCE_NUMBER + 1) / 2);
    }

    private boolean isNextOrderedSequence(int sequence) {
        final int nextOrdered = connection().sequenceState().nextOrdered();
        if (nextOrdered == -1 || nextOrdered > SequenceState.MAX_SEQUENCE_NUMBER) {
            connection().sequenceState().nextOrdered(sequence);
        }
        return sequence == connection().sequenceState().nextOrdered();
    }

    private List<UdpPacket> getNextOrderedPackets(int sequence) {
        final List<UdpPacket> packets = new ArrayList<>();

        UdpPacket packet;
        while ((packet = connection().pendingOrderedPackets().peek()) != null) {
            if (packet.sequence() == sequence) {
                packets.add(packet);
                connection().pendingOrderedPackets().remove(packet);
                sequence = (sequence + 1) % (SequenceState.MAX_SEQUENCE_NUMBER + 1);
            } else {
                break;
            }
        }

        return packets;
    }
}
