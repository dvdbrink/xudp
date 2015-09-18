package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.Protocol;
import com.danielvandenbrink.xudp.Server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UdpServer extends UdpEndpoint implements Server {
    private boolean listening;

    public void listen(int port) {
        socket.open();
        socket.bind(new InetSocketAddress(port));

        listening = true;
    }

    @Override
    public void send(Protocol protocol, byte[] data, SocketAddress to) {
        if (!listening) {
            throw new IllegalStateException("Server must be listening before sending data");
        }
        socket.send(protocol, data, to);
    }
}
