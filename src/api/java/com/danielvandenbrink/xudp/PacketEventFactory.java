package com.danielvandenbrink.xudp;

import java.net.SocketAddress;

public interface PacketEventFactory {
    PacketEvent create(Packet packet, SocketAddress address);
}
