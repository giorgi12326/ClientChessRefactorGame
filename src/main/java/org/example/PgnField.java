package org.example;


import org.example.dtos.Message;
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


public class PgnField implements Runnable {
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
            List<PGNParser.PGNMove> moveList;
            try {
                moveList = PGNParser.parseInList(PGNParser.parsePGN(pgn).get(0));
                System.out.println(moveList);
                GameWindow gameWindow = new GameWindow("bn", "wn", 0,0,0, moveList);

                new Thread(() -> {
                    try (Socket sock = new Socket("localhost", 8080);
                         ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
                         ObjectInputStream ois = new ObjectInputStream(sock.getInputStream())) {

                        Message msg = (new Message("pgn", pgn));
                        oos.writeObject(msg);
                        oos.flush();

                        Object reply = ois.readObject();
                        System.out.println(reply);
                        SwingUtilities.invokeLater(() -> {
                            if((Boolean) reply){
                                Piece piece = gameWindow.view.board[gameWindow.from.getX()][gameWindow.from.getY()].getPiece();
                                gameWindow.view.board[gameWindow.from.getX()][gameWindow.from.getY()].setPiece(null);
                                gameWindow.view.board[gameWindow.to.getX()][gameWindow.to.getY()].setPiece(piece);
                            }
                            gameWindow.view.repaint();
                        });

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
            new GameWindow("bn", "wn", 0, 0, 0, null);


            startWindow.dispose();
        });
        JButton loadButton1 = new JButton("Test Validity Of Games");
        loadButton1.setAlignmentX(Component.CENTER_ALIGNMENT);
        components.add(loadButton1);

        loadButton1.addActionListener(e -> {
            String pgn = pgnTextArea.getText();
            new GameWindow("bn", "wn", 0,0,0,null);

            startWindow.dispose();

        });

        startWindow.setVisible(true);
    }

}
