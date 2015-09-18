package com.danielvandenbrink.xudp.impl;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class UdpPacketEncoderTest {
    private static final int PACKET_SIZE = 64;

    private UdpPacketEncoder encoder;

    @Before
    public void setUp() {
        encoder = new UdpPacketEncoder();
    }

    @Test
    public void testUnreliablePacket() {
        UdpPacket packet = new UdpPacket(com.danielvandenbrink.xudp.Protocol.Unreliable);
        packet.ack(1);
        packet.ackBitField(2);
        packet.data(new byte[]{0, 0, 0, 0, 0, 0, 0, 0});

        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        encoder.encode(packet, buffer);
        buffer.flip();

        assertEquals(com.danielvandenbrink.xudp.Protocol.Unreliable.id(), buffer.getInt());
        assertEquals(1, buffer.getInt()); // Ack
        assertEquals(2, buffer.getInt()); // Ack bit field
        assertEquals(8, buffer.getInt()); // Message length
        assertEquals(0, buffer.getInt()); // Message data
        assertEquals(0, buffer.getInt());
    }

    @Test
    public void testUnreliableSequencedPacket() {
        UdpPacket packet = new UdpPacket(com.danielvandenbrink.xudp.Protocol.UnreliableSequenced);
        packet.ack(1);
        packet.ackBitField(2);
        packet.sequence(1);
        packet.data(new byte[]{0, 0, 0, 0, 0, 0, 0, 0});

        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        encoder.encode(packet, buffer);
        buffer.flip();

        assertEquals(com.danielvandenbrink.xudp.Protocol.UnreliableSequenced.id(), buffer.getInt());
        assertEquals(1, buffer.getInt()); // Ack
        assertEquals(2, buffer.getInt()); // Ack bit field
        assertEquals(1, buffer.getInt()); // Sequence number
        assertEquals(8, buffer.getInt()); // Message length
        assertEquals(0, buffer.getInt()); // Message data
        assertEquals(0, buffer.getInt());
    }

    @Test
    public void testReliableUnorderedPacket() {
        UdpPacket packet = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableUnordered);
        packet.ack(1);
        packet.ackBitField(2);
        packet.sequence(1);
        packet.timestamp(3);
        packet.data(new byte[]{0, 0, 0, 0, 0, 0, 0, 0});

        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        encoder.encode(packet, buffer);
        buffer.flip();

        assertEquals(com.danielvandenbrink.xudp.Protocol.ReliableUnordered.id(), buffer.getInt());
        assertEquals(1, buffer.getInt()); // Ack
        assertEquals(2, buffer.getInt()); // Ack bit field
        assertEquals(1, buffer.getInt()); // Sequence number
        assertEquals(3, buffer.getLong()); // Timestamp
        assertEquals(8, buffer.getInt()); // Message length
        assertEquals(0, buffer.getInt()); // Message data
        assertEquals(0, buffer.getInt());
    }

    @Test
    public void testReliableSequencedPacket() {
        UdpPacket packet = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableSequenced);
        packet.ack(2);
        packet.ackBitField(3);
        packet.sequence(1);
        packet.timestamp(4);
        packet.data(new byte[]{0, 0, 0, 0, 0, 0, 0, 0});

        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        encoder.encode(packet, buffer);
        buffer.flip();

        assertEquals(com.danielvandenbrink.xudp.Protocol.ReliableSequenced.id(), buffer.getInt());
        assertEquals(2, buffer.getInt()); // Ack
        assertEquals(3, buffer.getInt()); // Ack bit field
        assertEquals(1, buffer.getInt()); // Sequence number
        assertEquals(4, buffer.getLong()); // Timestamp
        assertEquals(8, buffer.getInt()); // Message length
        assertEquals(0, buffer.getInt()); // Message data
        assertEquals(0, buffer.getInt());
    }

    @Test
    public void testReliableOrderedPacket() {
        UdpPacket packet = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableOrdered);
        packet.ack(2);
        packet.ackBitField(3);
        packet.sequence(1);
        packet.timestamp(4);
        packet.data(new byte[]{0, 0, 0, 0, 0, 0, 0, 0});

        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        encoder.encode(packet, buffer);
        buffer.flip();

        assertEquals(com.danielvandenbrink.xudp.Protocol.ReliableOrdered.id(), buffer.getInt());
        assertEquals(2, buffer.getInt()); // Ack
        assertEquals(3, buffer.getInt()); // Ack bit field
        assertEquals(1, buffer.getInt()); // Sequence number
        assertEquals(4, buffer.getLong()); // Timestamp
        assertEquals(8, buffer.getInt()); // Message length
        assertEquals(0, buffer.getInt()); // Message data
        assertEquals(0, buffer.getInt());
    }

    @Test(expected = UdpPacketException.class)
    public void testUnknownProtocol() {
        UdpPacket packet = new UdpPacket(com.danielvandenbrink.xudp.Protocol.Unknown);
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        encoder.encode(packet, buffer);
    }
}
