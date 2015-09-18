package com.danielvandenbrink.xudp.impl;

class SequenceState {
    public static final int MAX_SEQUENCE_NUMBER = 255;

    private int nextLocalSequenceNumber = -1;
    private int remoteSequenceNumber = -1;
    private int nextOrderedSequenceNumber = -1;

    public int next() {
        nextLocalSequenceNumber = (nextLocalSequenceNumber + 1) % (SequenceState.MAX_SEQUENCE_NUMBER + 1);
        return nextLocalSequenceNumber;
    }

    public int remote() {
        return remoteSequenceNumber;
    }

    public void remote(int i) {
        remoteSequenceNumber = i % (SequenceState.MAX_SEQUENCE_NUMBER + 1);
    }

    public int nextOrdered() {
        return nextOrderedSequenceNumber;
    }

    public void nextOrdered(int i) {
        nextOrderedSequenceNumber = i % (SequenceState.MAX_SEQUENCE_NUMBER + 1);
    }
}
