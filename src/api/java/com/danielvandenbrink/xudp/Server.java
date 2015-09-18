package com.danielvandenbrink.xudp;

import java.net.SocketAddress;

public interface Server extends Endpoint {
    void listen(int port);
    void send(Protocol protocol, byte[] data, SocketAddress to);
}
