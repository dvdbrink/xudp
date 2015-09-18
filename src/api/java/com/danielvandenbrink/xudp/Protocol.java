package com.danielvandenbrink.xudp;

public enum Protocol {
    Unknown(-1),
    Unreliable(0),
    UnreliableSequenced(1),
    ReliableUnordered(2),
    ReliableSequenced(3),
    ReliableOrdered(4);

    private final int id;

    Protocol(final int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static Protocol fromId(final int id) {
        switch (id) {
            case 0: return Unreliable;
            case 1: return UnreliableSequenced;
            case 2: return ReliableUnordered;
            case 3: return ReliableSequenced;
            case 4: return ReliableOrdered;
            default: return Unknown;
        }
    }
}
