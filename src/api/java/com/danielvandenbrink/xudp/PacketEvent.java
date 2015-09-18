package com.danielvandenbrink.xudp;

import java.net.SocketAddress;

public interface PacketEvent {
    Packet packet();
    SocketAddress from();
}
