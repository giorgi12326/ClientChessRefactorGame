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

public class Main {
    static ObjectOutputStream out;
    private static ObjectInputStream in;
    public static StartMenu doRun;
    public static void main(String[] args) {
        doRun = new StartMenu();
        SwingUtilities.invokeLater(doRun);

        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 8080);
                 BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                System.out.println("Connected. Type messages:");

                while (true) {
                    System.out.print("> ");
                    String input = console.readLine();
                    if (input == null || input.equalsIgnoreCase("exit")) break;

                    List<String> str = new ArrayList<>();
                    str.add("Client");
                    str.add("Client");

                    SquareDto[][] object = (SquareDto[][]) sendObject(new Message("temp",str));
                    doRun.gameWindow.view.board = object;

                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            SquareDto squareDto = object[i][j];
                            if(squareDto!= null)
                                System.out.print(squareDto.getPiece() + " ");
                            else System.out.print("  ");
                        }
                        System.out.println();
                    }

                }
            } catch (IOException e) {
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