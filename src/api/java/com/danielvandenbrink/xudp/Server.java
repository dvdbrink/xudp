package com.danielvandenbrink.xudp;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Server<T extends Packet> extends Endpoint<T> {
    public Server(Socket socket, PacketHandler<T> packetHandler, PacketEncoder<T> packetEncoder,
                  PacketDecoder<T> packetDecoder, PacketFactory<T> packetFactory) {
        super(socket, packetHandler, packetEncoder, packetDecoder, packetFactory);
    }

    public void bind(int port) {
        socket().open();
        socket().bind(new InetSocketAddress(port));
    }

    public void send(Protocol protocol, byte[] data, SocketAddress to) {
        enqueue(protocol, data, to);
    }
}
