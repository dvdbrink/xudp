package com.danielvandenbrink.xudp;

public interface Client extends Endpoint {
    void connect(String ip, int port);
    void send(Protocol protocol, byte[] data);
}
