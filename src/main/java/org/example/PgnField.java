package org.example;


import org.example.dtos.Message;
import org.example.dtos.PGNMove;
import org.example.dtos.SquareDto;

import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.*;

import static org.example.Main.sendQueue;


public class PgnField implements Runnable {
    private PGNParser pgnParser = new PGNParser();

    public void run() {
        final JFrame startWindow = new JFrame("Chess");
        startWindow.setLocation(300, 100);
        startWindow.setResizable(false);
        startWindow.setSize(500, 600); // Slightly taller for PGN field

        Box components = Box.createVerticalBox();
        startWindow.add(components);

        // Game title
        final JPanel titlePanel = new JPanel();
        final JLabel titleLabel = new JLabel("Chess");
        titlePanel.add(titleLabel);
        components.add(titlePanel);

        // Player Panel
        final JPanel playerPanel = new JPanel();
        components.add(playerPanel);



        // PGN Input Field
        JPanel pgnPanel = new JPanel();
        pgnPanel.setLayout(new BorderLayout());
        JLabel pgnLabel = new JLabel("Enter PGN:");
        pgnPanel.add(pgnLabel, BorderLayout.NORTH);

        JTextArea pgnTextArea = new JTextArea(10, 40);
        pgnTextArea.setLineWrap(true);
        pgnTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(pgnTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        pgnPanel.add(scrollPane, BorderLayout.CENTER);
        components.add(pgnPanel);

        // Load PGN Button
        JButton loadButton = new JButton("Simulate One PGN");
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        components.add(loadButton);

        loadButton.addActionListener(e -> {
            String pgn = pgnTextArea.getText();

            boolean shouldTryToCheckParsed = true;
            List<PGNMove> moveList;
            try {
                moveList = pgnParser.parseInList(pgnParser.parsePGN(pgn).get(0));
                System.out.println(moveList);

                GameWindow gameWindow = new GameWindow("bn", "wn", 0,0,0, moveList);
                gameWindow.view.controller.pgn = true;


                new Thread(() -> {
                    try (Socket sock = new Socket("localhost", 8080);
                         ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
                         ObjectInputStream ois = new ObjectInputStream(sock.getInputStream())) {
                        while (true) {

                            Message msg = sendQueue.take();
                            oos.writeObject(msg);
                            oos.flush();

                            Object reply = ois.readObject();

                            if(reply instanceof Message m) {
                                if(m.payload instanceof String) {
                                    if (m.getPayload().equals("O-O")) {
                                        movePiece(gameWindow, gameWindow.view.board[gameWindow.whiteTurn ? 7 : 0][4], gameWindow.view.board[gameWindow.whiteTurn ? 7 : 0][6]);
                                        movePiece(gameWindow, gameWindow.view.board[gameWindow.whiteTurn ? 7 : 0][7], gameWindow.view.board[gameWindow.whiteTurn ? 7 : 0][5]);
                                    } else if (m.getPayload().equals("O-O-O")) {
                                        movePiece(gameWindow, gameWindow.view.board[gameWindow.whiteTurn ? 7 : 0][4], gameWindow.view.board[gameWindow.whiteTurn ? 7 : 0][2]);
                                        movePiece(gameWindow, gameWindow.view.board[gameWindow.whiteTurn ? 7 : 0][0], gameWindow.view.board[gameWindow.whiteTurn ? 7 : 0][3]);
                                    }
                                }
                                else if(m.payload instanceof SquareDto[] sq){
                                    Piece piece = gameWindow.view.board[sq[0].getY()][sq[0].getX()].getPiece();
                                    gameWindow.view.board[sq[0].getY()][sq[0].getX()].setPiece(null);
                                    gameWindow.view.board[sq[1].getY()][sq[1].getX()].setPiece(piece);
                                }
                                SwingUtilities.invokeLater(() -> {
                                    gameWindow.view.repaint();
                                });
                            }


                            gameWindow.whiteTurn = !gameWindow.whiteTurn;
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
            catch(NoSuchElementException ex){
                moveList = new ArrayList<>();
                System.err.println("please enter valid pgn format");
                shouldTryToCheckParsed = false;

            } catch (InvalidPropertiesFormatException ex) {
                shouldTryToCheckParsed = false;
                System.err.println("please enter valid pgn format");

            }

            startWindow.dispose();
        });
        JButton loadButton1 = new JButton("Test Validity Of Games");
        loadButton1.setAlignmentX(Component.CENTER_ALIGNMENT);
        components.add(loadButton1);

        loadButton1.addActionListener(e -> {
            String pgn = pgnTextArea.getText();

            boolean shouldTryToCheckParsed = true;
            List<PGNMove> moveList;
            try {
                moveList = pgnParser.parseInList(pgnParser.parsePGN(pgn).get(0));

                GameWindow gameWindow = new GameWindow("bn", "wn", 0,0,0, moveList);
                gameWindow.view.controller.pgn = true;


                new Thread(() -> {
                    try (Socket sock = new Socket("localhost", 8080);
                         ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
                         ObjectInputStream ois = new ObjectInputStream(sock.getInputStream())) {

                        List<String> payload = pgnParser.parsePGN(pgn);
                        Message msg = new Message("Multiple", payload);
                        oos.writeObject(msg);
                        oos.flush();

                        for (int i = 0; i < payload.size(); i++) {
                            Object reply = ois.readObject();
                            if(reply instanceof Message m){
                                System.out.println((Boolean) m.getPayload());
                            }

                        }


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
            catch(NoSuchElementException ex){
                moveList = new ArrayList<>();
                System.err.println("please enter valid pgn format");
                shouldTryToCheckParsed = false;

            } catch (InvalidPropertiesFormatException ex) {
                shouldTryToCheckParsed = false;
                System.err.println("please enter valid pgn format");

            }

            startWindow.dispose();

        });

        startWindow.setVisible(true);
    }

    private void movePiece(GameWindow gameWindow, Square from, Square to) {
        Piece piece = gameWindow.view.board[from.getX()][from.getY()].getPiece();
        gameWindow.view.board[from.getX()][from.getY()].setPiece(null);
        gameWindow.view.board[to.getX()][to.getY()].setPiece(piece);
    }

}
