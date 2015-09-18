package com.danielvandenbrink.xudp;

import java.nio.ByteBuffer;

public interface PacketEncoder<T extends Packet> {
    void encode(T packet, ByteBuffer byteBuffer);
}
