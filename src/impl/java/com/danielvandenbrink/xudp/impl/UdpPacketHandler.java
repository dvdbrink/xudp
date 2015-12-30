package com.danielvandenbrink.xudp.impl;

import com.danielvandenbrink.xudp.PacketHandler;
import com.danielvandenbrink.xudp.Protocol;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class UdpPacketHandler implements PacketHandler<UdpPacket> {
    private final Map<SocketAddress, EndpointState> endpointStates = new HashMap<>();

    @Override
    public List<UdpPacket> read(UdpPacket packet, SocketAddress from) {
        final EndpointState endpointState = getEndpointState(from);
        final UdpPacketReceiver packetReceiver = getPacketReceiver(packet.protocol(), endpointState);
        return packetReceiver.receive(packet);
    }

    @Override
    public List<UdpPacket> write(UdpPacket packet, SocketAddress to) {
        final EndpointState endpointState = getEndpointState(to);
        final UdpPacketSender packetSender = getPacketSender(packet.protocol(), endpointState);
        return packetSender.send(packet);
    }

    private EndpointState getEndpointState(final SocketAddress address) {
        if (endpointStates.containsKey(address)) {
            return endpointStates.get(address);
        }

        final EndpointState endpointState = new EndpointState();
        endpointStates.put(address, endpointState);
        return endpointState;
    }

    private UdpPacketReceiver getPacketReceiver(Protocol protocol, EndpointState endpointState) {
        switch (protocol) {
            case Unreliable: return new UnreliablePacketReceiver(endpointState);
            case UnreliableSequenced: return new UnreliableSequencedPacketReceiver(endpointState);
            case ReliableUnordered: return new ReliableUnorderedPacketReceiver(endpointState);
            case ReliableSequenced: return new ReliableSequencedPacketReceiver(endpointState);
            case ReliableOrdered: return new ReliableOrderedPacketReceiver(endpointState);
            default: throw new UdpPacketException("Invalid protocol");
        }
    }

    private UdpPacketSender getPacketSender(Protocol protocol, EndpointState endpointState) {
        switch (protocol) {
            case Unreliable: return new UnreliablePacketSender(endpointState);
            case UnreliableSequenced: return new UnreliableSequencedPacketSender(endpointState);
            case ReliableUnordered:
            case ReliableSequenced:
            case ReliableOrdered: return new ReliablePacketSender(endpointState);
            default: throw new UdpPacketException("Invalid protocol");
        }
    }
}
