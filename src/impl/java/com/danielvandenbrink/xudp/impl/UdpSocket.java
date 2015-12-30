package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

class UdpSocket implements Socket {
    public static final boolean CONFIGURE_BLOCKING = false;
    public static final boolean REUSE_ADDRESS = true;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final SelectorFactory selectorFactory;
    private final DatagramChannelFactory datagramChannelFactory;

    private Selector selector;
    private DatagramChannel channel;
    private SelectionKey selectionKey;

    private UdpSocketState socketState;
    private SocketAddress connectedFrom;

    public UdpSocket(SelectorFactory selectorFactory, DatagramChannelFactory datagramChannelFactory) {
        this.selectorFactory = selectorFactory;
        this.datagramChannelFactory = datagramChannelFactory;

        socketState = UdpSocketState.Uninitialized;
    }

    @Override
    public final void open() {
        selector = selectorFactory.create();
        channel = datagramChannelFactory.create();

        try {
            channel.configureBlocking(CONFIGURE_BLOCKING);
            selectionKey = channel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new UdpSocketException("Failed to open socket", e);
        }
    }

    @Override
    public final void bind(SocketAddress address) {
        try {
            channel.socket().setReuseAddress(REUSE_ADDRESS);
            channel.bind(address);
            socketState = UdpSocketState.Bound;
        } catch (IOException e) {
            throw new UdpSocketException("Failed to bind socket", e);
        }
    }

    @Override
    public final void interestOps(int ops) {
        selectionKey.interestOps(ops);
    }

    @Override
    public final void connect(SocketAddress address) {
        try {
            channel.connect(address);
            socketState = UdpSocketState.Connected;
            connectedFrom = address;
        } catch (IOException e) {
            throw new UdpSocketException("Failed to connect socket", e);
        }
    }

    @Override
    public final boolean select(int op) {
        try {
            selector.selectNow();

            if (op == SelectionKey.OP_READ && selectionKey.isReadable()) {
                return true;
            }

            if (op == SelectionKey.OP_WRITE && selectionKey.isValid() && selectionKey.isWritable()) {
                return true;
            }
        } catch (IOException e) {
            throw new UdpSocketException(e);
        }

        return false;
    }

    @Override
    public final SocketAddress read(ByteBuffer byteBuffer) {
        SocketAddress from = null;
        try {
            if (socketState == UdpSocketState.Bound) {
                from = channel.receive(byteBuffer);
            } else if (socketState == UdpSocketState.Connected && channel.isConnected()) {
                from = connectedFrom;
                channel.read(byteBuffer);
            }
            if (from != null && byteBuffer.limit() > 0) {
                log.trace("Read {} bytes from {}", byteBuffer.limit(), from);
            }
        } catch (IOException e) {
            throw new UdpSocketException("Error receiving bytes over channel", e);
        }
        return from;
    }

    @Override
    public final void write(ByteBuffer byteBuffer, SocketAddress to) {
        try {
            int bytes = 0;
            if (socketState == UdpSocketState.Bound) {
                bytes = channel.send(byteBuffer, to);
            } else if (socketState == UdpSocketState.Connected && channel.isConnected()) {
                bytes = channel.write(byteBuffer);
            }
            log.trace("Wrote {} bytes to {}", bytes, to);
        } catch (IOException e) {
            throw new UdpSocketException("Error sending bytes over channel", e);
        }
    }

    @Override
    public final void close() {
        try {
            if (channel != null) {
                if (channel.isConnected()) {
                    channel.disconnect();
                }
                channel.close();
            }

            if (selector != null) {
                selector.close();
            }
        } catch (IOException e) {
            throw new UdpSocketException("Failed to gracefully close socket", e);
        }
    }
}