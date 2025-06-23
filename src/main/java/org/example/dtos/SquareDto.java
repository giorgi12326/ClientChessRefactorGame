package org.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import javax.swing.*;
import java.io.Serializable;

@AllArgsConstructor
@Getter
public class SquareDto extends JComponent implements Serializable {
    int x;
    int y;
    char piece;
    int pieceColor;
}
