package com.danielvandenbrink.xudp;

import java.nio.channels.SelectionKey;

public interface SocketEventHandler extends EventHandler<SelectionKey> {
    void handle(SelectionKey key);
}
