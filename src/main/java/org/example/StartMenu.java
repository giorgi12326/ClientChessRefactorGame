package org.example;

import org.example.dtos.Message;
import org.example.dtos.SquareDto;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.example.Main.sendQueue;

public class StartMenu implements Runnable {
    public GameWindow gameWindow;

    @Override
    public void run() {
        final JFrame startWindow = new JFrame("Chess");
        startWindow.setLocation(300,100);
        startWindow.setResizable(false);
        startWindow.setSize(260, 240);

        Box components = Box.createVerticalBox();
        startWindow.add(components);

        // Game title
        JPanel titlePanel = new JPanel();
        components.add(titlePanel);
        titlePanel.add(new JLabel("Chess"));

        // Black player selections
        JPanel blackPanel = new JPanel();
        components.add(blackPanel);
        JLabel blackPiece = new JLabel();
        try {
            Image bp = ImageIO.read(getClass().getResource("/bp.png"));
            blackPiece.setIcon(new ImageIcon(bp));
        } catch (Exception e) {
            System.out.println("Required game file bp.png missing");
        }
        blackPanel.add(blackPiece);
        JTextField blackInput = new JTextField("Black", 10);
        blackPanel.add(blackInput);

        // White player selections
        JPanel whitePanel = new JPanel();
        components.add(whitePanel);
        JLabel whitePiece = new JLabel();
        try {
            Image wp = ImageIO.read(getClass().getResource("/wp.png"));
            whitePiece.setIcon(new ImageIcon(wp));
            startWindow.setIconImage(wp);
        } catch (Exception e) {
            System.out.println("Required game file wp.png missing");
        }
        whitePanel.add(whitePiece);
        JTextField whiteInput = new JTextField("White", 10);
        whitePanel.add(whiteInput);

        // Timer settings
        String[] minSecInts = new String[60];
        for (int i = 0; i < 60; i++) {
            minSecInts[i] = (i < 10 ? "0" : "") + i;
        }
        JComboBox<String> hours = new JComboBox<>(new String[]{"0","1","2","3"});
        JComboBox<String> minutes = new JComboBox<>(minSecInts);
        JComboBox<String> seconds = new JComboBox<>(minSecInts);
        Box timerSettings = Box.createHorizontalBox();
        hours.setMaximumSize(hours.getPreferredSize());
        minutes.setMaximumSize(minutes.getPreferredSize());
        seconds.setMaximumSize(seconds.getPreferredSize());
        timerSettings.add(hours);
        timerSettings.add(Box.createHorizontalStrut(5));
        timerSettings.add(minutes);
        timerSettings.add(Box.createHorizontalStrut(5));
        timerSettings.add(seconds);
        components.add(timerSettings);

        // Buttons container
        Box buttons = Box.createHorizontalBox();
        Box pgnContainer = Box.createHorizontalBox();

        JButton quit = new JButton("Quit");
        quit.addActionListener(e -> startWindow.dispose());

        JButton instr = new JButton("Instructions");
        instr.addActionListener(e ->
                JOptionPane.showMessageDialog(startWindow,
                        "To begin a new game, input player names\n" +
                                "next to the pieces. Set the clocks and\n" +
                                "click \"Start\". Setting the timer to all\n" +
                                "zeroes begins a new untimed game.",
                        "How to play",
                        JOptionPane.PLAIN_MESSAGE)
        );

        JButton start = new JButton("Start");
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bn = blackInput.getText();
                String wn = whiteInput.getText();
                int hh = Integer.parseInt((String) hours.getSelectedItem());
                int mm = Integer.parseInt((String) minutes.getSelectedItem());
                int ss = Integer.parseInt((String) seconds.getSelectedItem());

                gameWindow = new GameWindow(bn, wn, hh, mm, ss, null);

                // Socket thread
                new Thread(() -> {
                    try (Socket sock = new Socket("localhost", 8080);
                         ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
                         ObjectInputStream ois = new ObjectInputStream(sock.getInputStream())) {

                        while (true) {
                            Object reply = ois.readObject();

                            // 1) Move‐confirmation (Boolean)
                            if (reply instanceof Boolean moved) {
                                SwingUtilities.invokeLater(() -> {
                                    if (moved) {
                                        gameWindow.view.applyLastMove();
                                    }
                                    gameWindow.view.repaint();
                                });
                            }
                            // 2) PGN delivery
                            else if (reply instanceof Message msg && "deliverPGN".equals(msg.getType())) {
                                String pgn = (String) msg.getPayload();
                                SwingUtilities.invokeLater(() -> {
                                    JTextArea area = new JTextArea(pgn, 20, 40);
                                    area.setLineWrap(true);
                                    area.setWrapStyleWord(true);
                                    area.setEditable(false);
                                    JOptionPane.showMessageDialog(
                                            gameWindow.gameWindow,
                                            new JScrollPane(area),
                                            "Game PGN",
                                            JOptionPane.PLAIN_MESSAGE
                                    );
                                });
                            }

                            // send next user action
                            Message outMsg = sendQueue.take();
                            oos.writeObject(outMsg);
                            oos.flush();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();

                startWindow.dispose();
            }
        });

        JButton pgnParser = new JButton("pgnParser");
        pgnParser.addActionListener(e -> {
            new PgnField().run();
            startWindow.dispose();
        });

        buttons.add(start);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(instr);
        buttons.add(Box.createHorizontalStrut(10));
        buttons.add(quit);
        pgnContainer.add(pgnParser);

        components.add(buttons);
        components.add(pgnContainer);
        components.add(Box.createVerticalGlue());

        startWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startWindow.setVisible(true);
    }
}
