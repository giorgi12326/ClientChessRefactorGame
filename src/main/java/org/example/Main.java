package org.example;

import org.example.dtos.Message;
import org.example.dtos.SquareDto;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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

    public static Object sendObject(Object obj) {
        try {
            out.writeObject(obj);
            out.flush();
            Object o = in.readObject();
            doRun.gameWindow.view.repaint();
            return o;
        } catch (Exception e){
            throw new RuntimeException(e);
        }

    }
}