package com.danielvandenbrink.xudp;

import java.nio.ByteBuffer;

public interface PacketDecoder<T extends Packet> {
    T decode(ByteBuffer byteBuffer);
}
