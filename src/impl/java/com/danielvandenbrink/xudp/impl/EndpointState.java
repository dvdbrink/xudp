package com.danielvandenbrink.xudp.impl;

import java.util.*;

class EndpointState {
    private final Queue<UdpPacket> receivedPackets = new ArrayDeque<>();
    private final List<UdpPacket> pendingAcks = new ArrayList<>();
    private final PriorityQueue<UdpPacket> pendingOrderedPackets = new PriorityQueue<>(SequenceState.MAX_SEQUENCE_NUMBER * 2);
    private final SequenceState sequenceState = new SequenceState();

    public Queue<UdpPacket> receivedPackets() {
        return receivedPackets;
    }

    public void receivedPackets(UdpPacket packet) {
        if (receivedPackets.size() > UdpPacketDecoder.ACK_SIZE * 8) {
            receivedPackets.poll();
        }
        receivedPackets.offer(packet);
    }

    public List<UdpPacket> pendingAcks() {
        return pendingAcks;
    }

    public PriorityQueue<UdpPacket> pendingOrderedPackets() {
        return pendingOrderedPackets;
    }

    public SequenceState sequenceState() {
        return sequenceState;
    }
}

