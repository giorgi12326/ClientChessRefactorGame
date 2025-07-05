package org.example;

import org.example.dtos.Message;
import org.example.dtos.SquareDto;

import java.awt.*;
import java.awt.event.*;

public class Controller implements MouseListener, MouseMotionListener, KeyListener {
    View view;
    GameWindow gameWindow;

    public Controller(View view, GameWindow gameWindow) {
        this.view = view;
        this.gameWindow = gameWindow;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Square square = (Square) view.getComponentAt(new Point(e.getX(), e.getY()));
        gameWindow.from = square;
        gameWindow.currPiece = square.getPiece();
        Main.sendQueue.offer(new Message("mousePress", new SquareDto[]{
                new SquareDto(square.getX(), square.getY()), null
        }));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Square square = (Square) view.getComponentAt(new Point(e.getX(), e.getY()));
        gameWindow.to = square;
        Main.sendQueue.offer(new Message("mouseRelease", new SquareDto[]{
                null, new SquareDto(square.getX(), square.getY())
        }));
        gameWindow.currPiece = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        gameWindow.currX = e.getX() - 24;
        gameWindow.currY = e.getY() - 24;
        view.repaint();
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}