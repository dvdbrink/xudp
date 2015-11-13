package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.PacketEncoder;
import com.danielvandenbrink.xudp.Protocol;

import java.nio.ByteBuffer;

public class UdpPacketEncoder implements PacketEncoder<UdpPacket> {
    @Override
    public void encode(UdpPacket packet, ByteBuffer buffer) {
        buffer.putInt(packet.protocol().id());

        buffer.putInt(packet.ack());
        buffer.putInt(packet.ackBitField());

        switch (packet.protocol()) {
            case Unreliable: break;
            case UnreliableSequenced:
                buffer.putInt(packet.sequence());
                break;
            case ReliableUnordered:
            case ReliableSequenced:
            case ReliableOrdered:
                buffer.putInt(packet.sequence());
                buffer.putLong(packet.timestamp());
                break;
            default:
                throw new UdpPacketException("Invalid protocol");
        }

        buffer.putInt(packet.dataLength());
        buffer.put(packet.data());
    }
}
