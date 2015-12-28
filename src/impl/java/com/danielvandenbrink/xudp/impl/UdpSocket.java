package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

public class UdpSocket<T extends Packet> implements Socket {
    enum SocketState {
        Uninitialized,
        Bound,
        Connected
    };

    public static final int PACKET_SIZE = 2048;
    public static final boolean CONFIGURE_BLOCKING = false;
    public static final boolean REUSE_ADDRESS = true;

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(PACKET_SIZE);
    private final Queue<OutgoingPacket<T>> out = new ArrayDeque<>();

    private final PacketEncoder<T> encoder;
    private final PacketDecoder<T> decoder;
    private final PacketHandler<T> handler;

    private final SelectorFactory selectorFactory;
    private final DatagramChannelFactory datagramChannelFactory;
    private final PacketFactory<T> packetFactory;
    private final PacketEventFactory packetEventFactory;

    private Selector selector;
    private DatagramChannel datagramChannel;
    private SelectionKey selectionKey;

    private SocketState socketState;
    private SocketAddress connectedFrom;

    public UdpSocket(PacketEncoder<T> encoder, PacketDecoder<T> decoder, PacketHandler<T> handler,
                     SelectorFactory selectorFactory, DatagramChannelFactory datagramChannelFactory,
                     PacketFactory<T> packetFactory, PacketEventFactory packetEventFactory) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.handler = handler;
        this.selectorFactory = selectorFactory;
        this.datagramChannelFactory = datagramChannelFactory;
        this.packetFactory = packetFactory;
        this.packetEventFactory = packetEventFactory;

        socketState = SocketState.Uninitialized;
    }

    @Override
    public final void open() {
        selector = selectorFactory.create();
        datagramChannel = datagramChannelFactory.create();

        try {
            datagramChannel.configureBlocking(CONFIGURE_BLOCKING);
            selectionKey = datagramChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new UdpSocketException("Failed to open socket", e);
        }
    }

    @Override
    public final void bind(SocketAddress address) {
        try {
            datagramChannel.socket().setReuseAddress(REUSE_ADDRESS);
            datagramChannel.bind(address);
            socketState = SocketState.Bound;
        } catch (IOException e) {
            throw new UdpSocketException("Failed to connect on socket", e);
        }
    }

    @Override
    public final void connect(SocketAddress address) {
        try {
            datagramChannel.connect(address);
            socketState = SocketState.Connected;
            connectedFrom = address;
        } catch (IOException e) {
            throw new UdpSocketException("Failed to connect on socket", e);
        }
    }

    @Override
    public final void send(Protocol protocol, byte[] data) {
        if (socketState == SocketState.Connected) {
            send(protocol, data, connectedFrom);
        } else {
            throw new UdpSocketException("Unable to send data without recipient. Socket is not in a connected state, but a bound state.");
        }
    }

    @Override
    public final void send(Protocol protocol, byte[] data, SocketAddress to) {
        final T packet = packetFactory.create(protocol, data);
        final OutgoingPacket<T> outgoingPacket = new OutgoingPacket<>(packet, to);
        if (out.offer(outgoingPacket)) {
            selectionKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        } else {
            throw new UdpSocketException(String.format("Failed to queue message for %s", to));
        }
    }

    @Override
    public final void read(PacketEventHandler h) {
        select(true, false, h);
    }

    @Override
    public final void write() {
        select(false, true, null);
    }

    @Override
    public final void close() {
        try {
            if (datagramChannel != null) {
                if (datagramChannel.isConnected()) {
                    datagramChannel.disconnect();
                }
                datagramChannel.close();
            }

            if (selector != null) {
                selector.close();
            }
        } catch (IOException e) {
            throw new UdpSocketException("Unable to gracefully close the socket", e);
        }
    }

    private void select(boolean read, boolean write, PacketEventHandler h) {
        try {
            selector.selectNow();

            final Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                final SelectionKey key = keys.next();

                if (read && key.isReadable()) {
                    handleRead(key, h);
                    keys.remove();
                }

                if (write && key.isValid() && key.isWritable()) {
                    handleWrite(key);
                    keys.remove();
                }
            }
        } catch (IOException e) {
            throw new UdpSocketException(e);
        }
    }

    private void handleRead(SelectionKey key, PacketEventHandler h) {
        try {
            final DatagramChannel channel = (DatagramChannel) key.channel();

            while (true) {
                byteBuffer.clear();

                SocketAddress from = null;
                if (socketState == SocketState.Bound) {
                    from = channel.receive(byteBuffer);
                } else if (socketState == SocketState.Connected && channel.isConnected()) {
                    from = connectedFrom;
                    channel.read(byteBuffer);
                }
                byteBuffer.flip();

                if (from == null || byteBuffer.limit() == 0) {
                    break;
                } else {
                    log.trace("Read {} bytes from {}", byteBuffer.limit(), from);
                    handleReadByteBuffer(from, h);
                }
            }
        } catch (IOException e) {
            log.warn("Error receiving bytes over channel", e);
        }

        if (!out.isEmpty()) {
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
    }

    private void handleReadByteBuffer(SocketAddress from, PacketEventHandler h) {
        try {
            final T packet = decoder.decode(byteBuffer);
            final List<T> readablePackets = handler.read(packet, from);
            for (final T readablePacket : readablePackets) {
                h.handle(packetEventFactory.create(readablePacket, from));
            }
        } catch (UdpPacketException e) {
            log.error(String.format("Received corrupt packet from %s", from), e);
        }
    }

    private void handleWrite(SelectionKey key) {
        while (!out.isEmpty()) {
            final OutgoingPacket<T> msg = out.poll();
            handleWriteMessageObject(key, msg);
        }

        key.interestOps(SelectionKey.OP_READ);
    }

    private void handleWriteMessageObject(SelectionKey key, OutgoingPacket<T> msg) {
        try {
            final List<T> writablePackets = handler.write(msg.packet(), msg.address());
            for (final T writablePacket : writablePackets) {
                handleWritePacket(key, writablePacket, msg.address());
            }
        } catch (UdpPacketException e) {
            log.error(String.format("Unable to write packet to %s", msg.address()), e);
        }
    }

    private void handleWritePacket(SelectionKey key, T writablePacket, SocketAddress to) {
        byteBuffer.clear();
        try {
            encoder.encode(writablePacket, byteBuffer);
        } catch (UdpPacketException e) {
            log.error(String.format("Unable to encode packet for %s", to), e);
        }
        byteBuffer.flip();

        if (byteBuffer.remaining() > 0) {
            handleWriteBytes(key, to);
        } else {
            log.warn("Not sending empty packet to {}", to);
        }
    }

    private void handleWriteBytes(SelectionKey key, SocketAddress to) {
        try {
            final DatagramChannel channel = (DatagramChannel) key.channel();
            int bytes = 0;
            if (socketState == SocketState.Bound) {
                bytes = channel.send(byteBuffer, to);
            } else if (socketState == SocketState.Connected && channel.isConnected()) {
                bytes = channel.write(byteBuffer);
            }
            log.trace("Wrote {} bytes to {}", bytes, to);
        } catch (IOException e) {
            log.warn("Error sending bytes over channel", e);
        }
    }
}
