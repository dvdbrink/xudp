package com.danielvandenbrink.xudp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class Peer<T extends Packet> {
    public static final int PACKET_SIZE = 2048;

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(PACKET_SIZE);
    private final Queue<OutgoingPacket<T>> out = new ArrayDeque<>();

    private final Socket<T> socket;
    private final PacketHandler<T> handler;
    private final PacketEncoder<T> encoder;
    private final PacketDecoder<T> decoder;

    public Peer(Socket<T> socket, PacketHandler<T> handler, PacketEncoder<T> encoder, PacketDecoder<T> decoder) {
        this.socket = socket;
        this.handler = handler;
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public void connect(String ip, int port) {
        socket.connect(new InetSocketAddress(ip, port));
    }

    public void send() {

    }

    public void readAll(PacketEventHandler h) {
        try {
            final T packet = decoder.decode(byteBuffer);
            final List<T> readablePackets = handler.read(packet, from);
            for (final T readablePacket : readablePackets) {
                h.handle(packetEventFactory.create(readablePacket, from));
            }
        } catch (PacketException e) {
            log.error(String.format("Received corrupt packet from %s", from), e);
        }
    }

    public void writeAll() {
        socket.selectWrite(key -> {
            while (!out.isEmpty()) {
                final OutgoingPacket<T> msg = out.poll();
                handleWriteMessageObject(msg);
            }
        });
    }

    private void handleWriteMessageObject(OutgoingPacket<T> msg) {
        final List<T> writablePackets = handler.write(msg.packet(), msg.address());
        for (final T writablePacket : writablePackets) {
            handleWritePacket(writablePacket, msg.address());
        }
    }

    private void handleWritePacket(T writablePacket, SocketAddress to) {
        byteBuffer.clear();
        encoder.encode(writablePacket, byteBuffer)
        byteBuffer.flip();

        if (byteBuffer.remaining() > 0) {
            socket.write(byteBuffer, to);
        } else {
            log.warn("Not sending empty packet to {}", to);
        }
    }
}
