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
     static boolean baxusa = false;

    public static void main(String[] args) {
        doRun = new StartMenu();
        doRun.run();
        new Thread(() -> {
            try (Socket sock = new Socket("localhost", 8080);
                 ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
                 ObjectInputStream  ois = new ObjectInputStream(sock.getInputStream())) {

                while (true) {
                    Message msg = sendQueue.take();
                    oos.writeObject(msg);
                    oos.flush();

                    Object reply = ois.readObject();

                    // update GUI
                    SwingUtilities.invokeLater(() -> {
                        // assume reply is SquareDto[][]
                        doRun.gameWindow.view.board = (SquareDto[][]) reply;
                        doRun.gameWindow.view.repaint();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();


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