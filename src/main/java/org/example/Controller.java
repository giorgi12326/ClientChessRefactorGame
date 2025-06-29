package org.example;


import org.example.dtos.Message;
import org.example.dtos.SquareDto;

import java.awt.*;
import java.awt.event.*;

public class Controller implements MouseListener, KeyListener, MouseMotionListener {
    View view;
    GameWindow gameWindow;

    public Controller(View view,GameWindow gameWindow){
        this.gameWindow = gameWindow;
        this.view = view;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println(e.getX() + " " + e.getY());

        SquareDto squareDto = (SquareDto) view.getComponentAt(new Point(e.getX(), e.getY()));
        Main.sendQueue.offer(new Message("mousePress", squareDto));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println(e.getX() + " " + e.getY());
        SquareDto squareDto = (SquareDto) view.getComponentAt(new Point(e.getX(), e.getY()));
        Main.sendQueue.offer(new Message("mouseRelease", squareDto));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        gameWindow.currX = e.getX() - 24;
        gameWindow.currY = e.getY() - 24;
        view.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        SquareDto sq = (SquareDto) view.getComponentAt(new Point(e.getX(), e.getY()));
        gameWindow.currPiece = sq.getPiece();
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
//        board.reactToKeyPress(e);

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
