package org.example;

import org.example.dtos.Message;
import org.example.dtos.PGNMove;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BinaryOperator;

import javax.imageio.ImageIO;
import javax.swing.*;


public class GameWindow {
    public JFrame gameWindow;
    
    public Clock blackClock;
    public Clock whiteClock;
    public Square from;
    public Square to;

    private Timer timer;
    boolean turn = false;
    View view;

    public Piece currPiece;
    public int currX;
    public int currY;

    List<PGNMove> PGNList;

    String blackName;
    String whiteName;

    public boolean whiteTurn = true;



    public GameWindow(String blackName, String whiteName, int hh,
            int mm, int ss, List<PGNMove> PGNList) {
        this.PGNList = PGNList;
        this.blackName = blackName;
        this.whiteName = whiteName;
        
        blackClock = new Clock(hh, ss, mm);
        whiteClock = new Clock(hh, ss, mm);
        
        gameWindow = new JFrame("Chess");

        view = new View(this);

        try {
            Image whiteImg = ImageIO.read(getClass().getResource("/wp.png"));
            gameWindow.setIconImage(whiteImg);
        } catch (Exception e) {
            System.out.println("Game file wp.png not found");
        }

        gameWindow.setLocation(100, 100);
        
        
        gameWindow.setLayout(new BorderLayout(20,20));

//        this.board = new Board(this,PGN);

        // Game Data window
        JPanel gameData = gameDataPanel("board.blackName", "board.whiteName", hh, mm, ss);
        gameData.setSize(gameData.getPreferredSize());
        gameWindow.add(gameData, BorderLayout.NORTH);


        gameWindow.add(view, BorderLayout.CENTER);
        
        gameWindow.add(buttons(), BorderLayout.SOUTH);
        
        gameWindow.setMinimumSize(gameWindow.getPreferredSize());
        gameWindow.setSize(gameWindow.getPreferredSize());
        gameWindow.setResizable(false);
        
        gameWindow.pack();
        gameWindow.setVisible(true);
        gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
// Helper function to create data panel
    
    private JPanel gameDataPanel(final String bn, final String wn, 
            final int hh, final int mm, final int ss) {
        
        JPanel gameData = new JPanel();
        gameData.setLayout(new GridLayout(3,2,0,0));
        
        
        // PLAYER NAMES
        
        JLabel w = new JLabel(wn);
        JLabel b = new JLabel(bn);
        
        w.setHorizontalAlignment(JLabel.CENTER);
        w.setVerticalAlignment(JLabel.CENTER);
        b.setHorizontalAlignment(JLabel.CENTER);
        b.setVerticalAlignment(JLabel.CENTER);
        
        w.setSize(w.getMinimumSize());
        b.setSize(b.getMinimumSize());
        
        gameData.add(w);
        gameData.add(b);
        
        // CLOCKS
        
        final JLabel bTime = new JLabel(blackClock.getTime());
        final JLabel wTime = new JLabel(whiteClock.getTime());
        
        bTime.setHorizontalAlignment(JLabel.CENTER);
        bTime.setVerticalAlignment(JLabel.CENTER);
        wTime.setHorizontalAlignment(JLabel.CENTER);
        wTime.setVerticalAlignment(JLabel.CENTER);
        
        if (!(hh == 0 && mm == 0 && ss == 0)) {
            timer = new Timer(1000, null);
            timer.addActionListener(e -> {

                turn = !turn;
//                        board.getTurn();

                if (turn) {
                    whiteClock.decr();
                    wTime.setText(whiteClock.getTime());

                    if (whiteClock.outOfTime()) {
                        timer.stop();
                        int n = JOptionPane.showConfirmDialog(
                                gameWindow,
                                bn + " wins by time! Play a new game? \n" +
                                "Choosing \"No\" quits the game.",
                                bn + " wins!",
                                JOptionPane.YES_NO_OPTION);

                        if (n == JOptionPane.YES_OPTION) {
                            new GameWindow(bn, wn, hh, mm, ss,null);
                            gameWindow.dispose();
                        } else gameWindow.dispose();
                    }
                } else {
                    blackClock.decr();
                    bTime.setText(blackClock.getTime());

                    if (blackClock.outOfTime()) {
                        timer.stop();
                        int n = JOptionPane.showConfirmDialog(
                                gameWindow,
                                wn + " wins by time! Play a new game? \n" +
                                "Choosing \"No\" quits the game.",
                                wn + " wins!",
                                JOptionPane.YES_NO_OPTION);

                        if (n == JOptionPane.YES_OPTION) {
                            new GameWindow(bn, wn, hh, mm, ss, null);
                            gameWindow.dispose();
                        } else gameWindow.dispose();
                    }
                }
            });
            timer.start();
        } else {
            wTime.setText("Untimed game");
            bTime.setText("Untimed game");
        }
        
        gameData.add(wTime);
        gameData.add(bTime);
        
        gameData.setPreferredSize(gameData.getMinimumSize());
        
        return gameData;
    }
    
    private JPanel buttons() {
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 3, 10, 0));
        
        final JButton quit = new JButton("Quit");
        
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(
                        gameWindow,
                        "Are you sure you want to quit?",
                        "Confirm quit", JOptionPane.YES_NO_OPTION);
                
                if (n == JOptionPane.YES_OPTION) {
                    if (timer != null) timer.stop();
                    gameWindow.dispose();
                }
            }
          });
        
        final JButton nGame = new JButton("New game");
        
        nGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(
                        gameWindow,
                        "Are you sure you want to begin a new game?",
                        "Confirm new game", JOptionPane.YES_NO_OPTION);
                
                if (n == JOptionPane.YES_OPTION) {
                    SwingUtilities.invokeLater(new StartMenu());
                    gameWindow.dispose();
                }
            }
          });
        
        final JButton instr = new JButton("How to play");
        
        instr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(gameWindow,
                        "Move the chess pieces on the board by clicking\n"
                        + "and dragging. The game will watch out for illegal\n"
                        + "moves. You can win either by your opponent running\n"
                        + "out of time or by checkmating your opponent.\n"
                        + "\nGood luck, hope you enjoy the game!",
                        "How to play",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        JButton showPGN = new JButton("Show PGN");
        showPGN.addActionListener(e -> {
            String pgn = getFullPGN();

            SwingUtilities.invokeLater(() -> {
                JTextArea area = new JTextArea(pgn, 20, 40);
                area.setLineWrap(true);
                area.setWrapStyleWord(true);
                area.setEditable(false);
                JOptionPane.showMessageDialog(
                        gameWindow,
                        new JScrollPane(area),
                        "Game PGN",
                        JOptionPane.PLAIN_MESSAGE
                );
            });
        });
        
        buttons.add(instr);
        buttons.add(nGame);
        buttons.add(quit);
        buttons.add(showPGN);

        buttons.setPreferredSize(buttons.getMinimumSize());
        
        return buttons;
    }
    public String getPGNMovetext() {
        StringBuilder sb = new StringBuilder();
        int num = 1;
        for (int i = 0; i < StartMenu.pgnList.size(); i += 2) {
            sb.append(num++).append(". ").append(StartMenu.pgnList.get(i));
            if (i + 1 < StartMenu.pgnList.size()) sb.append(" ").append(StartMenu.pgnList.get(i + 1));
            if (i + 2 < StartMenu.pgnList.size()) sb.append(" ");
        }
        return sb.toString().trim();
    }

    public String getFullPGN() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Event \"?\"]\n")
                .append("[Site \"?\"]\n")
                .append("[Date \"").append(LocalDate.now()).append("\"]\n")
                .append("[Round \"?\"]\n")
                .append("[White \"").append(whiteName != null ? whiteName : "?").append("\"]\n")
                .append("[Black \"").append(blackName != null ? blackName : "?").append("\"]\n")
                .append(getPGNMovetext()).append(" ");
        return sb.toString();
    }
}
