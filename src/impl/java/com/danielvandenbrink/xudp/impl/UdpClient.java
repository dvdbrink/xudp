package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.*;

public class UdpClient extends Client<UdpPacket> {
    public UdpClient() {
        super(new UdpSocket(new SelectorFactory(),
                            new DatagramChannelFactory()),
              new UdpPacketHandler(),
              new UdpPacketEncoder(),
              new UdpPacketDecoder(),
              new UdpPacketFactory());
    }
}
