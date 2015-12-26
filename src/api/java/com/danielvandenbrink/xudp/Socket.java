package com.danielvandenbrink.xudp;

import java.net.SocketAddress;

public interface Socket {
    void open();
    void bind(SocketAddress address);
    void connect(SocketAddress address);
    void send(Protocol protocol, byte[] data);
    void send(Protocol protocol, byte[] data, SocketAddress to);
    void update();
    void read();
    void write();
    void close();
}
