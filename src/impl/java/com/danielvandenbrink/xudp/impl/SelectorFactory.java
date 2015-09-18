package com.danielvandenbrink.xudp.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.Selector;

class SelectorFactory {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Selector create() {
        try {
            return Selector.open();
        } catch (IOException e) {
            log.error("Failed to open selector", e);
        }
        return null;
    }
}
