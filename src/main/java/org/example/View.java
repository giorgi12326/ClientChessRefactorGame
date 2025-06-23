package org.example;

import org.example.dtos.SquareDto;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class View extends JPanel {
    SquareDto[][] board = new SquareDto[8][8];
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

    public View(){
        this.setPreferredSize(new Dimension(400, 400));
        this.setMaximumSize(new Dimension(400, 400));
        this.setMinimumSize(this.getPreferredSize());
        this.setSize(new Dimension(400, 400));
        setLayout(new GridLayout(8, 8, 0, 0));

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new SquareDto(i,j,'P',2);
            }
        }
        this.add(board[0][0]);

        controller = new Controller(this);

        addMouseMotionListener(controller);
        addMouseListener(controller);
        addKeyListener(controller);
        setFocusable(true);
        requestFocusInWindow();


        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int xMod = x % 2;
                int yMod = y % 2;

                if ((xMod == 0 && yMod == 0) || (xMod == 1 && yMod == 1)) {
                    this.add(board[x][y]);
                } else {
                    this.add(board[x][y]);
                }
            }
        }

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

                if(board[y][x] != null) {
                char piece = board[y][x].getPiece();

                    Image img = null;
                    try {
                        String pieceImage = "asd";
                        pieceImage = getPieceImage(y, x, piece, pieceImage);

                        img = ImageIO.read(getClass().getResource(pieceImage));
                    } catch (IOException e) {
                        System.out.println("baba");
                        throw new RuntimeException(e);
                    }
                    g.drawImage(img, px, py, tileSize, tileSize, null);
                }
            }
        }

//        if (board.getCurrPiece() != null) {
//            Piece curr = board.getCurrPiece();
//            if ((curr.getColor() == 1 && board.getTurn()) ||
//                    (curr.getColor() == 0 && !board.getTurn())) {
//                Image img = curr.getImage();
//                g.drawImage(img, board.currX, board.currY, tileSize, tileSize, null);
//            }
//        }


    }

    private String getPieceImage(int y, int x, char piece, String pieceImage) {
        if(board[y][x].getPieceColor()==0) {
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
