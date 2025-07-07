package org.example;

import org.example.dtos.Message;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;
import javax.swing.*;

import static org.example.Main.sendQueue;

public class StartMenu implements Runnable {
    public GameWindow gameWindow;
    public static List<String> pgnList = new ArrayList<>();

    public void run() {
        final JFrame startWindow = new JFrame("Chess");
        // Set window properties
        startWindow.setLocation(300,100);
        startWindow.setResizable(false);
        startWindow.setSize(260, 240);
        
        Box components = Box.createVerticalBox();
        startWindow.add(components);
        
        // Game title
        final JPanel titlePanel = new JPanel();
        components.add(titlePanel);
        final JLabel titleLabel = new JLabel("Chess");
        titlePanel.add(titleLabel);
        
        // Black player selections
        final JPanel blackPanel = new JPanel();
        components.add(blackPanel, BorderLayout.EAST);
        final JLabel blackPiece = new JLabel();
        try {
            Image blackImg = ImageIO.read(getClass().getResource("/bp.png"));
            blackPiece.setIcon(new ImageIcon(blackImg));
            blackPanel.add(blackPiece);
        } catch (Exception e) {
            System.out.println("Required game file bp.png missing");
        }

        final JTextField blackInput = new JTextField("Black", 10);
        blackPanel.add(blackInput);
        
        // White player selections
        final JPanel whitePanel = new JPanel();
        components.add(whitePanel);
        final JLabel whitePiece = new JLabel();
        
        try {
            Image whiteImg = ImageIO.read(getClass().getResource("/wp.png"));
            whitePiece.setIcon(new ImageIcon(whiteImg));
            whitePanel.add(whitePiece);
            startWindow.setIconImage(whiteImg);
        }  catch (Exception e) {
            System.out.println("Required game file wp.png missing");
        }
        
        
        final JTextField whiteInput = new JTextField("White", 10);
        whitePanel.add(whiteInput);
        
        // Timer settings
        final String[] minSecInts = new String[60];
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                minSecInts[i] = "0" + Integer.toString(i);
            } else {
                minSecInts[i] = Integer.toString(i);
            }
        }
        
        final JComboBox<String> seconds = new JComboBox<String>(minSecInts);
        final JComboBox<String> minutes = new JComboBox<String>(minSecInts);
        final JComboBox<String> hours = 
                new JComboBox<String>(new String[] {"0","1","2","3"});
        
        Box timerSettings = Box.createHorizontalBox();
        
        hours.setMaximumSize(hours.getPreferredSize());
        minutes.setMaximumSize(minutes.getPreferredSize());
        seconds.setMaximumSize(minutes.getPreferredSize());
        
        timerSettings.add(hours);
        timerSettings.add(Box.createHorizontalStrut(10));
        timerSettings.add(seconds);
        timerSettings.add(Box.createHorizontalStrut(10));
        timerSettings.add(minutes);
        
        timerSettings.add(Box.createVerticalGlue());
        
        components.add(timerSettings);
        
        // Buttons
        Box buttons = Box.createHorizontalBox();
        Box pgnContainer = Box.createHorizontalBox();
        final JButton quit = new JButton("Quit");
        
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              startWindow.dispose();
            }
          });
        
        final JButton instr = new JButton("Instructions");
        
        instr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(startWindow,
                        "To begin a new game, input player names\n" +
                        "next to the pieces. Set the clocks and\n" +
                        "click \"Start\". Setting the timer to all\n" +
                        "zeroes begins a new untimed game.",
                        "How to play",
                        JOptionPane.PLAIN_MESSAGE);
            }
          });
        
        final JButton start = new JButton("Start");
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String bn = blackInput.getText();
                String wn = whiteInput.getText();
                int hh = Integer.parseInt((String) hours.getSelectedItem());
                int mm = Integer.parseInt((String) minutes.getSelectedItem());
                int ss = Integer.parseInt((String) seconds.getSelectedItem());
                gameWindow = new GameWindow(bn, wn, hh, mm, ss,null);

                new Thread(() -> {
                    try (Socket sock = new Socket("localhost", 8080);
                        ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
                        ObjectInputStream ois = new ObjectInputStream(sock.getInputStream())) {

                        while (true) {

                            Message msg = sendQueue.take();
                            oos.writeObject(msg);
                            oos.flush();

                            Object reply = ois.readObject();

                            if(reply instanceof Message m){
                                SwingUtilities.invokeLater(() -> {
                                    pgnList.add((String)m.getPayload());
                                    Piece piece = gameWindow.view.board[gameWindow.from.getX()][gameWindow.from.getY()].getPiece();
                                    gameWindow.view.board[gameWindow.from.getX()][gameWindow.from.getY()].setPiece(null);
                                    gameWindow.view.board[gameWindow.to.getX()][gameWindow.to.getY()].setPiece(piece);
                                    gameWindow.view.repaint();
                                });
                            }
                            else{
                                SwingUtilities.invokeLater(() -> {
                                    if((Boolean) reply){
                                        Piece piece = gameWindow.view.board[gameWindow.from.getX()][gameWindow.from.getY()].getPiece();
                                        gameWindow.view.board[gameWindow.from.getX()][gameWindow.from.getY()].setPiece(null);
                                        gameWindow.view.board[gameWindow.to.getX()][gameWindow.to.getY()].setPiece(piece);
                                    }
                                    gameWindow.view.repaint();
                                });
                            }


                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();

                startWindow.dispose();
            }
        });

        final JButton pgnParser = new JButton("pgnParser");
        pgnParser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                new PgnField().run();
                startWindow.dispose();
            }
        });

        
        buttons.add(start);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(instr);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(quit);
        pgnContainer.add(pgnParser);
        components.add(buttons);
        components.add(pgnContainer);

        Component space = Box.createGlue();
        components.add(space);

        startWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startWindow.setVisible(true);
    }
}
