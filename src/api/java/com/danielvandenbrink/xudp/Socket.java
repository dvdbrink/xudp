package com.danielvandenbrink.xudp;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

public interface Socket {
    void open();
    void bind(SocketAddress address);
    void connect(SocketAddress address);
    void interestOps(int ops);
    boolean select(int ops);
    SocketAddress read(ByteBuffer byteBuffer);
    void write(ByteBuffer byteBuffer, SocketAddress to);
    void close();
}
