package org.example;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.io.Serializable;


@Setter
@Getter
public class Square extends JComponent implements Serializable {
    int x;
    int y;
    Piece piece;
    public Square(int x, int y){
        this.x = x;
        this.y = y;
    }
}