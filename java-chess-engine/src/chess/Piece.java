package chess;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import UI.ChessPanel;

public abstract class Piece {

    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public Color color;

    public Piece(int col, int row, Color color) {
        this.col = col;
        this.row = row;
        this.color = color;
        this.preCol = col;
        this.preRow = row;

        this.x = getPosX();
        this.y = getPosY();
    }

    public BufferedImage getImage(String name) {
        BufferedImage image = null;

        try {
            // To package your resources into a JAR file, place the res folder inside src
            // and use this function:
            // image = ImageIO.read(getClass().getResourceAsStream( path ));
            image = ImageIO.read(new File("res/pieces/" + name + ".png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public int getPosX() {
        return this.col * ChessPanel.tileSize;
    }

    public int getPosY() {
        return this.row * ChessPanel.tileSize;
    }

    public int getCol() {
        return (this.x + ChessPanel.tileSize / 2) / ChessPanel.tileSize;
    }

    public int getRow() {
        return (this.y + ChessPanel.tileSize / 2) / ChessPanel.tileSize;
    }
}
