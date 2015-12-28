package com.danielvandenbrink.xudp;

public interface EventHandler<T> {
    void handle(T e);
}
