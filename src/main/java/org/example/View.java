package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class View extends JPanel {
    private final GameWindow gameWindow;
    Square[][] board = new Square[8][8];
    public boolean turn = true;
    public Controller controller;

    private static final String RESOURCES_WBISHOP_PNG = "/wbishop.png";
    private static final String RESOURCES_BBISHOP_PNG = "/bbishop.png";
    private static final String RESOURCES_WKNIGHT_PNG = "/wknight.png";
    private static final String RESOURCES_BKNIGHT_PNG = "/bknight.png";
    private static final String RESOURCES_WROOK_PNG = "/wrook.png";
    private static final String RESOURCES_BROOK_PNG = "/brook.png";
    static final String RESOURCES_WKING_PNG = "/wking.png";
    static final String RESOURCES_BKING_PNG = "/bking.png";
    public static final String RESOURCES_BQUEEN_PNG = "/bqueen.png";
    public static final String RESOURCES_WQUEEN_PNG = "/wqueen.png";
    static final String RESOURCES_WPAWN_PNG = "/wpawn.png";
    private static final String RESOURCES_BPAWN_PNG = "/bpawn.png";
    Image img;

    public View(GameWindow gameWindow){
        img= null;
        try {
            img = ImageIO.read(getClass().getResource(RESOURCES_BPAWN_PNG));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.gameWindow = gameWindow;
        this.setPreferredSize(new Dimension(400, 400));
        this.setMaximumSize(new Dimension(400, 400));
        this.setMinimumSize(this.getPreferredSize());
        this.setSize(new Dimension(400, 400));
        setLayout(new GridLayout(8, 8, 0, 0));

        initBoard();
        this.add(board[0][0]);

        controller = new Controller(this,gameWindow);

        addMouseMotionListener(controller);
        addMouseListener(controller);
        addKeyListener(controller);
        setFocusable(true);
        requestFocusInWindow();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                this.add(board[x][y]);
            }
        }
    }

    private void initBoard() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                board[x][y] = new Square(x, y);
            }
        }

        for (int x = 0; x < 8; x++) {
            board[1][x].setPiece(new Piece(0, RESOURCES_BPAWN_PNG));
            board[6][x].setPiece(new Piece(1, RESOURCES_WPAWN_PNG));
        }

        board[7][3].setPiece(new Piece(0, RESOURCES_WQUEEN_PNG));
        board[0][3].setPiece(new Piece(1, RESOURCES_BQUEEN_PNG));

        board[0][4].setPiece(new Piece(0, RESOURCES_BKING_PNG));
        board[7][4].setPiece(new Piece(1, RESOURCES_WKING_PNG));

        board[0][0].setPiece(new Piece(0, RESOURCES_BROOK_PNG));
        board[0][7].setPiece(new Piece(0, RESOURCES_BROOK_PNG));
        board[7][0].setPiece(new Piece(1, RESOURCES_WROOK_PNG));
        board[7][7].setPiece(new Piece(1, RESOURCES_WROOK_PNG));

        board[0][1].setPiece(new Piece(0, RESOURCES_BKNIGHT_PNG));
        board[0][6].setPiece(new Piece(0, RESOURCES_BKNIGHT_PNG));
        board[7][1].setPiece(new Piece(1, RESOURCES_WKNIGHT_PNG));
        board[7][6].setPiece(new Piece(1, RESOURCES_WKNIGHT_PNG));

        board[0][2].setPiece(new Piece(0, RESOURCES_BBISHOP_PNG));
        board[0][5].setPiece(new Piece(0, RESOURCES_BBISHOP_PNG));
        board[7][2].setPiece(new Piece(1, RESOURCES_WBISHOP_PNG));
        board[7][5].setPiece(new Piece(1, RESOURCES_WBISHOP_PNG));

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int tileSize = 50;

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if ((y+x)%2 == 1) {
                    g.setColor(new Color(221,192,127));
                } else {
                    g.setColor(new Color(101,67,33));
                }

                int px = x * tileSize;
                int py = y * tileSize;

                g.fillRect(px, py, tileSize, tileSize);

                Piece curr = board[y][x].getPiece();
                if(curr != null && gameWindow.currPiece != curr) {
                    Image img = curr.getImg();
                    g.drawImage(img, px, py, tileSize, tileSize, null);
                }
            }
        }

        if (gameWindow.currPiece != null) {
            Piece curr = gameWindow.currPiece;
            g.drawImage(curr.img, gameWindow.currX, gameWindow.currY, tileSize, tileSize, null);

        }


    }

    private String getPieceImage(int y, int x, char piece, String pieceImage) {
        if(board[y][x].getPiece().getColor()==0) {
            if (piece == 'P')
                pieceImage = RESOURCES_BPAWN_PNG;
            else if (piece == 'B')
                pieceImage = RESOURCES_BBISHOP_PNG;
            else if (piece == 'Q')
                pieceImage = RESOURCES_BQUEEN_PNG;
            else if (piece == 'K')
                pieceImage = RESOURCES_BKING_PNG;
            else if (piece == 'N')
                pieceImage = RESOURCES_BKNIGHT_PNG;
            else if (piece == 'R')
                pieceImage = RESOURCES_BROOK_PNG;
        }
        else{
            if (piece == 'P')
                pieceImage = RESOURCES_WPAWN_PNG;
            else if (piece == 'B')
                pieceImage = RESOURCES_WBISHOP_PNG;
            else if (piece == 'Q')
                pieceImage = RESOURCES_WQUEEN_PNG;
            else if (piece == 'K')
                pieceImage = RESOURCES_WKING_PNG;
            else if (piece == 'N')
                pieceImage = RESOURCES_WKNIGHT_PNG;
            else if (piece == 'R')
                pieceImage = RESOURCES_WROOK_PNG;
        }
        return pieceImage;
    }
}
