package com.danielvandenbrink.xudp.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.DatagramChannel;

class DatagramChannelFactory {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public DatagramChannel create() {
        try {
            return DatagramChannel.open();
        } catch (IOException e) {
            log.error("Failed to open datagram channel", e);
        }
        return null;
    }
}
