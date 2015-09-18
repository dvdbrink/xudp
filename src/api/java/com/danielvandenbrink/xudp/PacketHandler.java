package com.danielvandenbrink.xudp;

import java.net.SocketAddress;
import java.util.List;

public interface PacketHandler<T extends Packet> {
    List<T> read(T packet, SocketAddress from);
    List<T> write(T packet, SocketAddress to);
}
