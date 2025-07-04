package org.example;

import org.example.dtos.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static final BlockingQueue<Message> sendQueue = new LinkedBlockingQueue<>();

    static ObjectOutputStream out;
    static ObjectInputStream in;
    public static StartMenu doRun;

    public static void main(String[] args) {
        doRun = new StartMenu();
        doRun.run();
    }
}