package com.danielvandenbrink.xudp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public abstract class Endpoint<T extends Packet> {
    public static final int PACKET_SIZE = 2048;

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(PACKET_SIZE);
    private final Queue<Message<T>> out = new ArrayDeque<>();

    private final Socket socket;
    private final PacketHandler<T> packetHandler;
    private final PacketEncoder<T> packetEncoder;
    private final PacketDecoder<T> packetDecoder;
    private final PacketFactory<T> packetFactory;

    public Endpoint(Socket socket, PacketHandler<T> packetHandler, PacketEncoder<T> packetEncoder,
                    PacketDecoder<T> packetDecoder, PacketFactory<T> packetFactory) {
        this.socket = socket;
        this.packetHandler = packetHandler;
        this.packetEncoder = packetEncoder;
        this.packetDecoder = packetDecoder;
        this.packetFactory = packetFactory;
    }

    public Socket socket() {
        return socket;
    }

    public void read(MessageHandler messageHandler) {
        if (socket.select(SelectionKey.OP_READ)) {
            while (true) {
                byteBuffer.clear();
                SocketAddress from = socket.read(byteBuffer);
                byteBuffer.flip();

                if (from == null || byteBuffer.limit() == 0) {
                    break;
                } else {
                    handleReadByteBuffer(messageHandler, from);
                }
            }

            if (!out.isEmpty()) {
                socket.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            }
        }
    }

    public void write() {
        if (socket.select(SelectionKey.OP_WRITE)) {
            while (!out.isEmpty()) {
                final Message<T> msg = out.poll();
                handleWriteMessageObject(msg);
            }

            socket.interestOps(SelectionKey.OP_READ);
        }
    }

    public void close() {
        socket.close();
    }

    protected void enqueue(Protocol protocol, byte[] data, SocketAddress to) {
        final T packet = packetFactory.create(protocol, data);
        final Message<T> message = new Message<>(packet, to);
        if (out.offer(message)) {
            socket.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        } else {
            throw new SocketException(String.format("Failed to queue message for %s", to));
        }
    }

    private void handleReadByteBuffer(MessageHandler h, SocketAddress from) {
        final T packet = packetDecoder.decode(byteBuffer);
        final List<T> readablePackets = packetHandler.read(packet, from);
        for (final T readablePacket : readablePackets) {
            h.handle(new Message<>(readablePacket, from));
        }
    }

    private void handleWriteMessageObject(Message<T> msg) {
        final List<T> writablePackets = packetHandler.write(msg.packet(), msg.from());
        for (final T writablePacket : writablePackets) {
            handleWritePacket(writablePacket, msg.from());
        }
    }

    private void handleWritePacket(T writablePacket, SocketAddress to) {
        byteBuffer.clear();
        packetEncoder.encode(writablePacket, byteBuffer);
        byteBuffer.flip();

        if (byteBuffer.remaining() > 0) {
            socket.write(byteBuffer, to);
        } else {
            log.warn("Not sending empty packet to {}", to);
        }
    }
}
