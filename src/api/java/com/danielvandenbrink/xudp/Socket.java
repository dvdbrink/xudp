package com.danielvandenbrink.xudp;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

public interface Socket<T extends Packet> {
    void open();
    void bind(SocketAddress address);
    void connect(SocketAddress address);
    void send(Protocol protocol, byte[] data);
    void send(Protocol protocol, byte[] data, SocketAddress to);
    void read(PacketEventHandler handler);
    void write(ByteBuffer b, SocketAddress to);
    void selectWrite(SocketEventHandler e);
    void close();
}
