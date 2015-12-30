package com.danielvandenbrink.xudp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public abstract class Endpoint<T extends Packet> {
    public static final int MAX_PACKET_SIZE = 2048;

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ByteBuffer buffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
    private final Queue<Message<T>> out = new ArrayDeque<>();
    private final List<MessageEvent> messageHandlers = new ArrayList<>();

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

    public void onMessage(MessageEvent messageEvent) {
        messageHandlers.add(messageEvent);
    }

    public void read() {
        if (socket.select(SelectionKey.OP_READ)) {
            while (true) {
                buffer.clear();
                SocketAddress from = socket.read(buffer);
                buffer.flip();

                if (from == null || buffer.limit() == 0) {
                    break;
                } else {
                    handleReadByteBuffer(from);
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
                handleWriteMessage(msg);
            }

            socket.interestOps(SelectionKey.OP_READ);
        }
    }

    public void close() {
        socket.close();
    }

    protected Socket socket() {
        return socket;
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

    private void handleReadByteBuffer(SocketAddress from) {
        final T packet = packetDecoder.decode(buffer);
        final List<T> readablePackets = packetHandler.read(packet, from);
        for (final T readablePacket : readablePackets) {
            handleReadMessage(new Message<>(readablePacket, from));
        }
    }

    private void handleReadMessage(Message<T> msg) {
        for (final MessageEvent messageEvent : messageHandlers) {
            messageEvent.handle(msg);
        }
    }

    private void handleWriteMessage(Message<T> msg) {
        final List<T> writablePackets = packetHandler.write(msg.packet(), msg.from());
        for (final T writablePacket : writablePackets) {
            handleWritePacket(writablePacket, msg.from());
        }
    }

    private void handleWritePacket(T writablePacket, SocketAddress to) {
        buffer.clear();
        packetEncoder.encode(writablePacket, buffer);
        buffer.flip();

        if (buffer.remaining() > 0) {
            socket.write(buffer, to);
        } else {
            log.warn("Not sending empty packet to {}", to);
        }
    }
}
