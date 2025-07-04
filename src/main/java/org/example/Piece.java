package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

@Getter
public class Piece {
    int color;
    Image img;
    String imgPath;

    public Piece(int color, String imgPath){
        this.color = color;
        this.imgPath = imgPath;
        try {
            img = ImageIO.read(getClass().getResource(imgPath));
        } catch (IOException e) {
            System.err.println("IMAGE NOT FOUND!!!");
            throw new RuntimeException(e);
        }
    }
}