package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.*;

public class UdpServer extends Server<UdpPacket> {
    public UdpServer() {
        super(new UdpSocket(new SelectorFactory(),
                            new DatagramChannelFactory()),
              new UdpPacketHandler(),
              new UdpPacketEncoder(),
              new UdpPacketDecoder(),
              new UdpPacketFactory());
    }
}
