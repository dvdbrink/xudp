package com.danielvandenbrink.xudp.impl;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class UdpPacketDecoderTest {
    private static final int PACKET_SIZE = 64;

    private UdpPacketDecoder decoder;

    @Before
    public void setUp() {
        decoder = new UdpPacketDecoder();
    }

    @Test
    public void testUnreliablePacket() {
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        buffer.putInt(com.danielvandenbrink.xudp.Protocol.Unreliable.id());
        buffer.putInt(2); // Ack
        buffer.putInt(3); // Ack bit field
        buffer.putInt(8); // Message length
        buffer.putInt(0); // Message data
        buffer.putInt(0);
        buffer.flip();

        UdpPacket packet = decoder.decode(buffer);
        assertEquals(com.danielvandenbrink.xudp.Protocol.Unreliable, packet.protocol());
        assertEquals(2, packet.ack());
        assertEquals(3, packet.ackBitField());
        assertEquals(8, packet.data().length);
    }

    @Test
    public void testUnreliableSequencedPacket() {
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        buffer.putInt(com.danielvandenbrink.xudp.Protocol.UnreliableSequenced.id());
        buffer.putInt(2); // Ack
        buffer.putInt(3); // Ack bit field
        buffer.putInt(1); // Sequence number
        buffer.putInt(8); // Message length
        buffer.putInt(0); // Message data
        buffer.putInt(0);
        buffer.flip();

        UdpPacket packet = decoder.decode(buffer);
        assertEquals(com.danielvandenbrink.xudp.Protocol.UnreliableSequenced, packet.protocol());
        assertEquals(2, packet.ack());
        assertEquals(3, packet.ackBitField());
        assertEquals(1, packet.sequence());
        assertEquals(8, packet.data().length);
    }

    @Test
    public void testReliableUnorderedPacket() {
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        buffer.putInt(com.danielvandenbrink.xudp.Protocol.ReliableUnordered.id());
        buffer.putInt(2); // Ack
        buffer.putInt(3); // Ack bit field
        buffer.putInt(1); // Sequence number
        buffer.putLong(0); // Timestamp
        buffer.putInt(8); // Message length
        buffer.putInt(0); // Message data
        buffer.putInt(0);
        buffer.flip();

        UdpPacket packet = decoder.decode(buffer);
        assertEquals(com.danielvandenbrink.xudp.Protocol.ReliableUnordered, packet.protocol());
        assertEquals(2, packet.ack());
        assertEquals(3, packet.ackBitField());
        assertEquals(1, packet.sequence());
        assertEquals(0, packet.timestamp());
        assertEquals(8, packet.data().length);
    }

    @Test
    public void testReliableSequencedPacket() {
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        buffer.putInt(com.danielvandenbrink.xudp.Protocol.ReliableSequenced.id());
        buffer.putInt(2); // Ack
        buffer.putInt(3); // Ack bit field
        buffer.putInt(1); // Sequence number
        buffer.putLong(0); // Timestamp
        buffer.putInt(8); // Message length
        buffer.putInt(0); // Message data
        buffer.putInt(0);
        buffer.flip();

        UdpPacket packet = decoder.decode(buffer);
        assertEquals(com.danielvandenbrink.xudp.Protocol.ReliableSequenced, packet.protocol());
        assertEquals(2, packet.ack());
        assertEquals(3, packet.ackBitField());
        assertEquals(1, packet.sequence());
        assertEquals(0, packet.timestamp());
        assertEquals(8, packet.data().length);
    }

    @Test
    public void testReliableOrderedPacket() {
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        buffer.putInt(com.danielvandenbrink.xudp.Protocol.ReliableOrdered.id());
        buffer.putInt(2); // Ack
        buffer.putInt(3); // Ack bit field
        buffer.putInt(1); // Sequence number
        buffer.putLong(0); // Timestamp
        buffer.putInt(8); // Message length
        buffer.putInt(0); // Message data
        buffer.putInt(0);
        buffer.flip();

        UdpPacket packet = decoder.decode(buffer);
        assertEquals(com.danielvandenbrink.xudp.Protocol.ReliableOrdered, packet.protocol());
        assertEquals(2, packet.ack());
        assertEquals(3, packet.ackBitField());
        assertEquals(1, packet.sequence());
        assertEquals(0, packet.timestamp());
        assertEquals(8, packet.data().length);
    }

    @Test(expected = UdpPacketException.class)
    public void testInvalidProtocol() {
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        buffer.flip();

        decoder.decode(buffer);
    }

    @Test(expected = UdpPacketException.class)
    public void testUnknownProtocol() {
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        buffer.putInt(com.danielvandenbrink.xudp.Protocol.Unknown.id());
        buffer.flip();

        decoder.decode(buffer);
    }

    @Test(expected = UdpPacketException.class)
    public void testInvalidSequenceNumber() {
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        buffer.putInt(com.danielvandenbrink.xudp.Protocol.UnreliableSequenced.id());
        buffer.flip();

        decoder.decode(buffer);
    }

    @Test(expected = UdpPacketException.class)
    public void testInvalidAckData() {
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        buffer.putInt(com.danielvandenbrink.xudp.Protocol.ReliableUnordered.id());
        buffer.putInt(1); // Sequence number
        buffer.flip();

        decoder.decode(buffer);
    }

    @Test(expected = UdpPacketException.class)
    public void testInvalidMessageLength() {
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        buffer.putInt(com.danielvandenbrink.xudp.Protocol.ReliableSequenced.id());
        buffer.putInt(1); // Sequence number
        buffer.putInt(2); // Ack
        buffer.putInt(3); // Ack bit field
        buffer.putLong(0); // Timestamp
        buffer.flip();

        decoder.decode(buffer);
    }

    @Test(expected = UdpPacketException.class)
    public void testMessageTooBig() {
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        buffer.putInt(com.danielvandenbrink.xudp.Protocol.ReliableSequenced.id());
        buffer.putInt(1); // Sequence number
        buffer.putInt(2); // Ack
        buffer.putInt(3); // Ack bit field
        buffer.putLong(0); // Timestamp
        buffer.putInt(4); // Message length
        buffer.putInt(0); // Message data
        buffer.putInt(0);
        buffer.flip();

        decoder.decode(buffer);
    }

    @Test(expected = UdpPacketException.class)
    public void testMessageTooSmall() {
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        buffer.putInt(com.danielvandenbrink.xudp.Protocol.ReliableSequenced.id());
        buffer.putInt(1); // Sequence number
        buffer.putInt(2); // Ack
        buffer.putInt(3); // Ack bit field
        buffer.putLong(0); // Timestamp
        buffer.putInt(9); // Message length
        buffer.putInt(0); // Message data
        buffer.putInt(0);
        buffer.flip();

        decoder.decode(buffer);
    }
}
