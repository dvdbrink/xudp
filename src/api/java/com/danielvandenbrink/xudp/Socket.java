package com.danielvandenbrink.xudp;

import java.net.SocketAddress;

public interface Socket<T extends Packet> {
    void open();
    void bind(SocketAddress address);
    void connect(SocketAddress address);
    void send(Protocol protocol, byte[] data, SocketAddress to);
    void update();
    void close();
}
