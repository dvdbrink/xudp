package com.danielvandenbrink.xudp.api;

import com.danielvandenbrink.xudp.Protocol;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProtocolTest {
    @Test
    public void testFromId() {
        Assert.assertEquals(Protocol.Unknown, Protocol.fromId(Protocol.Unknown.id()));
        assertEquals(Protocol.Unreliable, Protocol.fromId(Protocol.Unreliable.id()));
        assertEquals(Protocol.UnreliableSequenced, Protocol.fromId(Protocol.UnreliableSequenced.id()));
        assertEquals(Protocol.ReliableUnordered, Protocol.fromId(Protocol.ReliableUnordered.id()));
        assertEquals(Protocol.ReliableSequenced, Protocol.fromId(Protocol.ReliableSequenced.id()));
        assertEquals(Protocol.ReliableOrdered, Protocol.fromId(Protocol.ReliableOrdered.id()));
    }
}
