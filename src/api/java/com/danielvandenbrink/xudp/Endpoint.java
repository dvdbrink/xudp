package com.danielvandenbrink.xudp;

public interface Endpoint extends Runnable {
    void onPacket(PacketEventHandler packetHandler);

    void start();
    void stop();
}
