package org.example;

import org.example.dtos.Message;

import javax.swing.*;
import java.awt.*;

public class GameWindow {
    public final JFrame gameWindow;
    public Clock blackClock;
    public Clock whiteClock;
    public Square from, to;
    private Timer timer;
    public View view;
    public String blackName, whiteName;
    public Piece currPiece;
    public int currX, currY;
    public Controller controller;
    public GameWindow(String blackName, String whiteName, int hh, int mm, int ss, String PGN) {
        this.blackName = blackName;
        this.whiteName = whiteName;
        blackClock = new Clock(hh, mm, ss);
        whiteClock = new Clock(hh, mm, ss);

        gameWindow = new JFrame("Chess");

        view = new View(this);

        controller = new Controller(view, this);
        view.addMouseListener(controller);
        view.addMouseMotionListener(controller);
        view.addKeyListener(controller);

        gameWindow.setLayout(new BorderLayout(20,20));
        gameWindow.add(view, BorderLayout.CENTER);
        gameWindow.add(buttons(), BorderLayout.SOUTH);
        gameWindow.pack();
        gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameWindow.setVisible(true);
    }

    private JPanel buttons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton instr = new JButton("How to play");
        instr.addActionListener(e ->
                JOptionPane.showMessageDialog(gameWindow,
                        "Move the chess pieces by dragging them.\n" +
                                "Illegal moves are blocked automatically.",
                        "How to play", JOptionPane.PLAIN_MESSAGE)
        );

        JButton nGame = new JButton("New game");
        nGame.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(
                    gameWindow,
                    "Begin a new game?",
                    "Confirm new game",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                SwingUtilities.invokeLater(new StartMenu());
                gameWindow.dispose();
            }
        });

        JButton quit = new JButton("Quit");
        quit.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(
                    gameWindow,
                    "Quit?",
                    "Confirm quit",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                gameWindow.dispose();
            }
        });

        JButton showPGN = new JButton("Show PGN");
        showPGN.addActionListener(e -> {
            // request the PGN from server
            Main.sendQueue.offer(new Message("requestPGN", null));
        });

        buttons.add(instr);
        buttons.add(nGame);
        buttons.add(quit);
        buttons.add(showPGN);  // NEW: Show PGN button

        return buttons;
    }
}
