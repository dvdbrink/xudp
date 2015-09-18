package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.Client;
import com.danielvandenbrink.xudp.Protocol;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UdpClient extends UdpEndpoint implements Client {
    private SocketAddress serverAddress;

    public void connect(String ip, int port) {
        serverAddress = new InetSocketAddress(ip, port);

        socket.open();
        socket.connect(serverAddress);
    }

    @Override
    public void send(Protocol protocol, byte[] data) {
        if (serverAddress == null) {
            throw new IllegalStateException("Client must be connected before sending data");
        }
        socket.send(protocol, data, serverAddress);
    }
}
