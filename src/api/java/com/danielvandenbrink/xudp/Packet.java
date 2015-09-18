package com.danielvandenbrink.xudp;

public interface Packet {
    Protocol protocol();
    byte[] data();
}
