package com.danielvandenbrink.xudp.impl;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class UdpPacketHandlerTest {
    private UdpPacketHandler handler;

    @Before
    public void setUp() {
        handler = new UdpPacketHandler();
    }

    @Test
    public void testUnreliablePacket() {
        UdpPacket packet = new UdpPacket(com.danielvandenbrink.xudp.Protocol.Unreliable);
        List<UdpPacket> packets = handler.read(packet, null);
        assertEquals(1, packets.size());
        assertEquals(packet, packets.get(0));

        packet = new UdpPacket(com.danielvandenbrink.xudp.Protocol.Unreliable);
        packets = handler.write(packet, null);
        assertEquals(1, packets.size());
        assertEquals(packet, packets.get(0));
    }

    @Test
    public void testUnreliableSequencedPacket() {
        UdpPacket packet = new UdpPacket(com.danielvandenbrink.xudp.Protocol.UnreliableSequenced);
        List<UdpPacket> packets = handler.write(packet, null);
        assertEquals(1, packets.size());
        assertEquals(packet, packets.get(0));
        assertEquals(relativeSequence(0), packets.get(0).sequence());

        packet = new UdpPacket(com.danielvandenbrink.xudp.Protocol.UnreliableSequenced);
        packet.sequence(2);
        packets = handler.read(packet, null);
        assertEquals(1, packets.size());
        assertEquals(packet, packets.get(0));

        packet = new UdpPacket(com.danielvandenbrink.xudp.Protocol.UnreliableSequenced);
        packet.sequence(1);
        packets = handler.read(packet, null);
        assertEquals(0, packets.size());

        packet = new UdpPacket(com.danielvandenbrink.xudp.Protocol.UnreliableSequenced);
        packet.sequence(3);
        packets = handler.read(packet, null);
        assertEquals(1, packets.size());
        assertEquals(packet, packets.get(0));
    }

    @Test
    public void testReliableUnorderedPacket() {
        // We get p0
        UdpPacket p0 = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableUnordered);
        p0.sequence(10);
        List<UdpPacket> packets = handler.read(p0, null);
        assertEquals(1, packets.size());
        assertEquals(p0, packets.get(0));

        // We get p1
        UdpPacket p1 = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableUnordered);
        p1.sequence(12);
        packets = handler.read(p1, null);
        assertEquals(1, packets.size());
        assertEquals(p1, packets.get(0));

        // We get ack for p1 and p0 in the ack bit field
        UdpPacket p2 = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableUnordered);
        packets = handler.write(p2, null);
        assertEquals(1, packets.size());
        assertEquals(p2, packets.get(0));
        assertEquals(relativeSequence(0), packets.get(0).sequence());
        assertEquals(relativeSequence(12), packets.get(0).ack());
        assertEquals(2, packets.get(0).ackBitField());

        // We get p1 and p2, because p1 has not been acked yet
        UdpPacket p3 = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableUnordered);
        packets = handler.write(p3, null);
        assertEquals(2, packets.size());
        assertEquals(p2, packets.get(0));
        assertEquals(relativeSequence(0), packets.get(0).sequence());
        assertEquals(relativeSequence(12), packets.get(0).ack());
        assertEquals(2, packets.get(0).ackBitField());
        assertEquals(p3, packets.get(1));
        assertEquals(relativeSequence(1), packets.get(1).sequence());
        assertEquals(relativeSequence(12), packets.get(1).ack());
        assertEquals(2, packets.get(1).ackBitField());

        // Ack packets p2 and p3 and we get p4
        UdpPacket p4 = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableUnordered);
        p4.sequence(11);
        p4.ack(1);
        p4.ackBitField(1);
        packets = handler.read(p4, null);
        assertEquals(1, packets.size());

        // We get 1 packet because everything is acked
        UdpPacket p5 = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableUnordered);
        packets = handler.write(p5, null);
        assertEquals(1, packets.size());
        assertEquals(p5, packets.get(0));
    }

    @Test
    public void testReliableSequencedPacket() {
        // We get p0
        UdpPacket p0 = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableSequenced);
        p0.sequence(10);
        List<UdpPacket> packets = handler.read(p0, null);
        assertEquals(1, packets.size());
        assertEquals(p0, packets.get(0));

        // We get p1
        UdpPacket p1 = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableSequenced);
        p1.sequence(12);
        packets = handler.read(p1, null);
        assertEquals(1, packets.size());
        assertEquals(p1, packets.get(0));

        // We get ack for p1 and p0 in the ack bit field
        UdpPacket p2 = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableSequenced);
        packets = handler.write(p2, null);
        assertEquals(1, packets.size());
        assertEquals(p2, packets.get(0));
        assertEquals(relativeSequence(0), packets.get(0).sequence());
        assertEquals(relativeSequence(12), packets.get(0).ack());
        assertEquals(2, packets.get(0).ackBitField());

        // We get p1 and p2, because p1 has not been acked yet
        UdpPacket p3 = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableSequenced);
        packets = handler.write(p3, null);
        assertEquals(2, packets.size());
        assertEquals(p2, packets.get(0));
        assertEquals(relativeSequence(0), packets.get(0).sequence());
        assertEquals(relativeSequence(12), packets.get(0).ack());
        assertEquals(2, packets.get(0).ackBitField());
        assertEquals(p3, packets.get(1));
        assertEquals(relativeSequence(1), packets.get(1).sequence());
        assertEquals(relativeSequence(12), packets.get(1).ack());
        assertEquals(2, packets.get(1).ackBitField());

        // Ack packets p2 and p3 and we get nothing
        UdpPacket p4 = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableSequenced);
        p4.sequence(11);
        p4.ack(1);
        p4.ackBitField(1);
        packets = handler.read(p4, null);
        assertEquals(0, packets.size());

        // We get 1 packet because everything is acked
        UdpPacket p5 = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableSequenced);
        packets = handler.write(p5, null);
        assertEquals(1, packets.size());
        assertEquals(p5, packets.get(0));
    }

    @Test
    public void testReliableOrderedPacket() {
        UdpPacket firstPacket = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableOrdered);
        firstPacket.sequence(1);
        firstPacket.timestamp(0);

        List<UdpPacket> packets = handler.read(firstPacket, null);
        assertEquals(1, packets.size());
        assertEquals(firstPacket, packets.get(0));

        UdpPacket earlyPacket = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableOrdered);
        earlyPacket.sequence(3);
        earlyPacket.timestamp(2);
        packets = handler.read(earlyPacket, null);
        assertEquals(0, packets.size());

        UdpPacket wayTooEarlyPacket = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableOrdered);
        wayTooEarlyPacket.sequence(6);
        wayTooEarlyPacket.timestamp(5);
        packets = handler.read(wayTooEarlyPacket, null);
        assertEquals(0, packets.size());

        UdpPacket latePacket = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableOrdered);
        latePacket.sequence(2);
        latePacket.timestamp(1);
        packets = handler.read(latePacket, null);
        assertEquals(2, packets.size());
        assertEquals(latePacket, packets.get(0));
        assertEquals(earlyPacket, packets.get(1));

        wayTooEarlyPacket = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableOrdered);
        wayTooEarlyPacket.sequence(5);
        wayTooEarlyPacket.timestamp(4);
        packets = handler.read(wayTooEarlyPacket, null);
        assertEquals(0, packets.size());

        wayTooEarlyPacket = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableOrdered);
        wayTooEarlyPacket.sequence(4);
        wayTooEarlyPacket.timestamp(3);
        packets = handler.read(wayTooEarlyPacket, null);
        assertEquals(3, packets.size());
    }

    @Test
    public void testMaxSequenceNumber() {
        for (int i = 0; i <= (SequenceState.MAX_SEQUENCE_NUMBER + 1) * 2; ++i) {
            final int expectedSequenceNumber = relativeSequence(i);
            UdpPacket writePacket = new UdpPacket(com.danielvandenbrink.xudp.Protocol.ReliableUnordered);
            List<UdpPacket> writtenPackets = handler.write(writePacket, null);
            assertEquals(i + 1, writtenPackets.size());
            assertEquals(writePacket, writtenPackets.get(i));
            assertEquals(expectedSequenceNumber, writtenPackets.get(i).sequence());
        }
    }

    @Test(expected = UdpPacketException.class)
    public void testReadInvalidPacket() {
        UdpPacket packet = new UdpPacket(com.danielvandenbrink.xudp.Protocol.Unknown);
        handler.read(packet, null);
    }

    @Test(expected = UdpPacketException.class)
    public void testWriteInvalidPacket() {
        UdpPacket packet = new UdpPacket(com.danielvandenbrink.xudp.Protocol.Unknown);
        handler.write(packet, null);
    }

    private int relativeSequence(final int sequence) {
        return sequence % (SequenceState.MAX_SEQUENCE_NUMBER + 1);
    }
}
