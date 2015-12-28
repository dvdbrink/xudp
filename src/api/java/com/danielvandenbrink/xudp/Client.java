package com.danielvandenbrink.xudp;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Client<T extends Packet> extends Endpoint<T> {
    private SocketAddress to;

    public Client(Socket socket, PacketHandler<T> packetHandler, PacketEncoder<T> packetEncoder,
                  PacketDecoder<T> packetDecoder, PacketFactory<T> packetFactory) {
        super(socket, packetHandler, packetEncoder, packetDecoder, packetFactory);
    }

    public void connect(String ip, int port) {
        to = new InetSocketAddress(ip, port);

        socket().open();
        socket().connect(to);
    }

    public void send(Protocol protocol, byte[] data) {
        enqueue(protocol, data, to);
    }
}
