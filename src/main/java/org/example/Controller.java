package org.example;


import org.example.dtos.Message;
import org.example.dtos.SquareDto;

import java.awt.*;
import java.awt.event.*;

public class Controller implements MouseListener, KeyListener, MouseMotionListener {
    View view;
    public Controller(View view){
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
//        board.reactToMouseDragged(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
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
