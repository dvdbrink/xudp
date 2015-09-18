package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.Endpoint;
import com.danielvandenbrink.xudp.PacketEvent;
import com.danielvandenbrink.xudp.PacketEventHandler;

import java.util.ArrayList;
import java.util.List;

public class UdpEndpoint implements Endpoint {
    private final List<PacketEventHandler> packetHandlers = new ArrayList<>();

    protected final UdpSocket<UdpPacket> socket;

    private boolean running;

    public UdpEndpoint() {
        socket = new UdpSocket<UdpPacket>(new UdpPacketEncoder(), new UdpPacketDecoder(), new UdpPacketHandler(),
                new SelectorFactory(), new DatagramChannelFactory(), new UdpPacketFactory(),
                new UdpPacketEventFactory()) {
            @Override
            public void handlePacketEvent(PacketEvent packetEvent) {
                for (final PacketEventHandler handler : packetHandlers) {
                    handler.handle(packetEvent);
                }
            }
        };
    }

    @Override
    public void onPacket(PacketEventHandler packetHandler) {
        packetHandlers.add(packetHandler);
    }

    @Override
    public void start() {
        new Thread(this, getClass().getSimpleName()).start();
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            socket.update();
        }
        socket.close();
    }
}
