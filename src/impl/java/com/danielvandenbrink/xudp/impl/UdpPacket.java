package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.Packet;
import com.danielvandenbrink.xudp.Protocol;

class UdpPacket implements Packet, Comparable<UdpPacket> {
    private Protocol protocol;
    private int ack;
    private int ackBitField;
    private int sequence;
    private long timestamp;
    private byte[] data = new byte[0];

    public UdpPacket(final Protocol protocol) {
        protocol(protocol);
    }

    public UdpPacket(final Protocol protocol, final byte[] data) {
        protocol(protocol);
        data(data);
    }

    public Protocol protocol() {
        return protocol;
    }

    public void protocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public int ack() {
        return ack;
    }

    public void ack(int ack) {
        this.ack = ack;
    }

    public int ackBitField() {
        return ackBitField;
    }

    public void ackBitField(int ackBitField) {
        this.ackBitField = ackBitField;
    }

    public int sequence() {
        return sequence;
    }

    public void sequence(int sequence) {
        this.sequence = sequence % (SequenceState.MAX_SEQUENCE_NUMBER + 1);
    }

    public long timestamp() {
        return timestamp;
    }

    public void timestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] data() {
        final byte[] copy = new byte[data.length];
        System.arraycopy(data, 0, copy, 0, data.length);
        return copy;
    }

    public void data(byte[] copy) {
        data = new byte[copy.length];
        System.arraycopy(copy, 0, data, 0, copy.length);
    }

    public int dataLength() {
        return data.length;
    }

    @Override
    public String toString() {
        return String.format("{ Protocol: %s, Sequence: %s, Ack: %s, Ack bits: %s, Timestamp: %s, Data length: %s }",
                protocol(), sequence(), ack(), Integer.toBinaryString(ackBitField()), timestamp(), dataLength());
    }

    @Override
    public int compareTo(UdpPacket other) {
        if (timestamp() > other.timestamp()) {
            return 1;
        }
        else if (timestamp() < other.timestamp()) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
