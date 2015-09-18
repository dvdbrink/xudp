package com.danielvandenbrink.xudp;

public interface PacketFactory<T extends Packet> {
    T create(Protocol protocol, byte[] data);
}
