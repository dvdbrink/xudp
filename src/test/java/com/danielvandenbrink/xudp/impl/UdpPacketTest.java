package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.Protocol;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class UdpPacketTest {
    @Test
    public void testProtocolConstructor() {
        UdpPacket packet = new UdpPacket(Protocol.Unreliable);
        assertEquals(Protocol.Unreliable, packet.protocol());
    }

    @Test
    public void testProtocolDataConstructor() {
        byte[] data = new byte[] { 1, 2, 3, 4 };
        UdpPacket packet = new UdpPacket(Protocol.Unreliable, data);
        assertArrayEquals(data, packet.data());
    }

    @Test
    public void testSetProtocol() {
        UdpPacket packet = new UdpPacket(Protocol.Unreliable);
        packet.protocol(Protocol.UnreliableSequenced);
        assertEquals(Protocol.UnreliableSequenced, packet.protocol());
    }

    @Test
    public void testSetSequenceNumber() {
        UdpPacket packet = new UdpPacket(Protocol.Unreliable);
        packet.sequence(1234);
        Assert.assertEquals(1234 % (SequenceState.MAX_SEQUENCE_NUMBER + 1), packet.sequence());
    }

    @Test
    public void testSetAck() {
        UdpPacket packet = new UdpPacket(Protocol.Unreliable);
        packet.ack(1234);
        assertEquals(1234, packet.ack());
    }

    @Test
    public void testSetAckBitField() {
        UdpPacket packet = new UdpPacket(Protocol.Unreliable);
        packet.ackBitField(1234);
        assertEquals(1234, packet.ackBitField());
    }

    @Test
    public void testSetData() {
        byte[] data = new byte[] { 1, 2, 3, 4 };
        UdpPacket packet = new UdpPacket(Protocol.Unreliable);
        packet.data(data);
        assertArrayEquals(data, packet.data());
    }

    @Test
    public void testToString() {
        UdpPacket packet = new UdpPacket(Protocol.Unreliable);
        assertEquals("{ Protocol: Unreliable, Sequence: 0, Ack: 0, Ack bits: 0, Timestamp: 0, Data length: 0 }", packet.toString());
    }
}
