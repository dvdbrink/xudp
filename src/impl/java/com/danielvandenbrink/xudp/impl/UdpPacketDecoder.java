package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.PacketDecoder;
import com.danielvandenbrink.xudp.Protocol;

import java.nio.ByteBuffer;

public class UdpPacketDecoder implements PacketDecoder<UdpPacket> {
    public static final int PROTOCOL_SIZE = 4;
    public static final int MESSAGE_LENGTH_SIZE = 4;
    public static final int SEQUENCE_NUMBER_SIZE = 4;
    public static final int ACK_SIZE = 4;
    public static final int ACK_BIT_FIELD_SIZE = 4;
    public static final int ACK_DATA_SIZE = ACK_SIZE + ACK_BIT_FIELD_SIZE;
    public static final int TIMESTAMP_SIZE = 8;

    public UdpPacket decode(ByteBuffer buffer) {
        if (buffer.remaining() >= PROTOCOL_SIZE) {
            final UdpPacket packet = new UdpPacket(Protocol.fromId(buffer.getInt()));

            decodeAckData(buffer, packet);

            switch (packet.protocol()) {
                case Unreliable: break;
                case UnreliableSequenced:
                    decodeSequenceNumber(buffer, packet);
                    break;
                case ReliableUnordered:
                case ReliableSequenced:
                case ReliableOrdered:
                    decodeSequenceNumber(buffer, packet);
                    decodeTimestamp(buffer, packet);
                    break;
                default:
                    throw new UdpPacketException("Invalid protocol");
            }

            decodeMessageData(buffer, packet);

            return packet;
        } else {
            throw new UdpPacketException("Unable to decode protocol");
        }
    }

    private void decodeAckData(ByteBuffer buffer, UdpPacket packet) {
        if (buffer.remaining() >= ACK_DATA_SIZE) {
            packet.ack(buffer.getInt());
            packet.ackBitField(buffer.getInt());
        } else {
            throw new UdpPacketException("Unable to decode ack data");
        }
    }

    private void decodeSequenceNumber(ByteBuffer buffer, UdpPacket packet) {
        if (buffer.remaining() >= SEQUENCE_NUMBER_SIZE) {
            packet.sequence(buffer.getInt());
        } else {
            throw new UdpPacketException("Unable to decode sequence number");
        }
    }

    private void decodeTimestamp(ByteBuffer buffer, UdpPacket packet) {
        if (buffer.remaining() >= TIMESTAMP_SIZE) {
            packet.timestamp(buffer.getLong());
        } else {
            throw new UdpPacketException("Unable to decode timestamp");
        }
    }

    private void decodeMessageData(ByteBuffer buffer, UdpPacket packet) {
        if (buffer.remaining() >= MESSAGE_LENGTH_SIZE) {
            final int messageLength = buffer.getInt();
            if (buffer.remaining() == messageLength) {
                final byte[] message = new byte[messageLength];
                buffer.get(message);
                packet.data(message);
            } else {
                throw new UdpPacketException("Remaining bytes does not equal message length");
            }
        } else {
            throw new UdpPacketException("Unable to decode message length");
        }
    }
}
